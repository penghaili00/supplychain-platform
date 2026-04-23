package com.supplychain.service.provider.customer.service;

import com.supplychain.common.core.enums.common.EnableStatus;
import com.supplychain.service.api.customer.command.CustomerAppAccountCreateCommand;
import com.supplychain.service.api.customer.command.CustomerAppAccountUpdateCommand;
import com.supplychain.service.api.customer.command.CustomerContactCreateCommand;
import com.supplychain.service.provider.app.user.entity.AppUser;
import com.supplychain.service.provider.app.user.mapper.AppUserMapper;
import com.supplychain.service.provider.auth.support.AppPasswordHasher;
import com.supplychain.service.provider.customer.entity.Customer;
import com.supplychain.service.provider.customer.entity.CustomerContact;
import com.supplychain.service.provider.customer.mapper.CustomerMapper;
import com.supplychain.service.provider.rbac.entity.AppUserRole;
import com.supplychain.service.provider.rbac.entity.SysRole;
import com.supplychain.service.provider.rbac.mapper.AppUserRoleMapper;
import com.supplychain.service.provider.rbac.mapper.SysRoleMapper;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CustomerAdminServiceTest {

    private final CustomerMapper customerMapper = mock(CustomerMapper.class);
    private final CustomerService customerService = mock(CustomerService.class);
    private final CustomerContactService customerContactService = mock(CustomerContactService.class);
    private final CustomerAddressService customerAddressService = mock(CustomerAddressService.class);
    private final AppUserMapper appUserMapper = mock(AppUserMapper.class);
    private final SysRoleMapper sysRoleMapper = mock(SysRoleMapper.class);
    private final AppUserRoleMapper appUserRoleMapper = mock(AppUserRoleMapper.class);
    private final AppPasswordHasher appPasswordHasher = mock(AppPasswordHasher.class);

    private final CustomerAdminService service = new CustomerAdminService(
            customerMapper,
            customerService,
            customerContactService,
            customerAddressService,
            appUserMapper,
            sysRoleMapper,
            appUserRoleMapper,
            appPasswordHasher
    );

    @Test
    void shouldCreateAppAccountWithDefaultRole() {
        Customer customer = customer(8001L);
        SysRole role = appRole(3002L, "app_user");
        when(customerService.getById(8001L)).thenReturn(customer);
        when(appUserMapper.selectByUsername("buyer.demo")).thenReturn(null);
        when(appPasswordHasher.encode(eq("Secret@123"), anyString())).thenReturn("encoded-hash");
        when(appUserMapper.insert(any(AppUser.class))).thenAnswer(invocation -> {
            AppUser user = invocation.getArgument(0);
            user.setId(9001L);
            return 1;
        });
        when(sysRoleMapper.selectByRoleKey("app_user")).thenReturn(role);
        when(sysRoleMapper.selectByIds(List.of(3002L))).thenReturn(List.of(role));
        when(appUserRoleMapper.insert(any(AppUserRole.class))).thenReturn(1);

        CustomerAppAccountCreateCommand command = new CustomerAppAccountCreateCommand();
        command.setUsername("buyer.demo");
        command.setPassword("Secret@123");
        command.setDisplayName("演示采购员");

        Long userId = service.createAppAccount(8001L, command);

        assertThat(userId).isEqualTo(9001L);

        ArgumentCaptor<AppUser> userCaptor = ArgumentCaptor.forClass(AppUser.class);
        verify(appUserMapper).insert(userCaptor.capture());
        AppUser createdUser = userCaptor.getValue();
        assertThat(createdUser.getCustomerId()).isEqualTo(8001L);
        assertThat(createdUser.getStatus()).isEqualTo(1);
        assertThat(createdUser.getDisplayName()).isEqualTo("演示采购员");
        assertThat(createdUser.getPasswordSalt()).isNotBlank();
        assertThat(createdUser.getPasswordHash()).isEqualTo("encoded-hash");

        ArgumentCaptor<AppUserRole> roleCaptor = ArgumentCaptor.forClass(AppUserRole.class);
        verify(appUserRoleMapper).insert(roleCaptor.capture());
        assertThat(roleCaptor.getValue().getUserId()).isEqualTo(9001L);
        assertThat(roleCaptor.getValue().getRoleId()).isEqualTo(3002L);
    }

    @Test
    void shouldPreserveExistingRolesWhenUpdatingAppAccountWithoutRoleIds() {
        Customer customer = customer(8001L);
        AppUser user = new AppUser();
        user.setId(9001L);
        user.setCustomerId(8001L);
        user.setUsername("buyer.demo");
        user.setDisplayName("旧名称");
        user.setStatus(1);

        AppUserRole relation = new AppUserRole();
        relation.setUserId(9001L);
        relation.setRoleId(3010L);

        SysRole role = appRole(3010L, "customer_buyer");
        when(customerService.getById(8001L)).thenReturn(customer);
        when(appUserMapper.selectByUserId(9001L)).thenReturn(user);
        when(appUserMapper.selectByUsername("buyer.demo")).thenReturn(user);
        when(appUserMapper.updateById(any(AppUser.class))).thenReturn(1);
        when(appUserRoleMapper.selectList(any())).thenReturn(List.of(relation));
        when(sysRoleMapper.selectByIds(List.of(3010L))).thenReturn(List.of(role));
        when(appUserRoleMapper.insert(any(AppUserRole.class))).thenReturn(1);

        CustomerAppAccountUpdateCommand command = new CustomerAppAccountUpdateCommand();
        command.setUsername("buyer.demo");
        command.setDisplayName("新名称");

        service.updateAppAccount(8001L, 9001L, command);

        verify(appUserMapper).updateById(user);
        assertThat(user.getDisplayName()).isEqualTo("新名称");
        assertThat(user.getStatus()).isEqualTo(1);

        ArgumentCaptor<AppUserRole> roleCaptor = ArgumentCaptor.forClass(AppUserRole.class);
        verify(appUserRoleMapper).insert(roleCaptor.capture());
        assertThat(roleCaptor.getValue().getRoleId()).isEqualTo(3010L);
    }

    @Test
    void shouldDefaultFirstContactToPrimaryWhenFlagOmitted() {
        Customer customer = customer(8001L);
        when(customerService.getById(8001L)).thenReturn(customer);
        when(customerContactService.listByCustomerId(8001L))
                .thenReturn(List.of())
                .thenReturn(List.of(contact(7001L, 8001L, 1, EnableStatus.ENABLED)));
        doAnswer(invocation -> {
            CustomerContact saved = invocation.getArgument(0);
            saved.setId(7001L);
            return 7001L;
        }).when(customerContactService).create(any(CustomerContact.class));

        CustomerContactCreateCommand command = new CustomerContactCreateCommand();
        command.setContactName("张三");
        command.setMobile("13800000000");

        Long contactId = service.createContact(8001L, command);

        assertThat(contactId).isEqualTo(7001L);

        ArgumentCaptor<CustomerContact> contactCaptor = ArgumentCaptor.forClass(CustomerContact.class);
        verify(customerContactService).create(contactCaptor.capture());
        CustomerContact created = contactCaptor.getValue();
        assertThat(created.getCustomerId()).isEqualTo(8001L);
        assertThat(created.getIsDefault()).isEqualTo(1);
        assertThat(created.getStatus()).isEqualTo(EnableStatus.ENABLED);
        verify(customerContactService, never()).update(any(CustomerContact.class));
    }

    private Customer customer(Long customerId) {
        Customer customer = new Customer();
        customer.setId(customerId);
        customer.setCustomerCode("CUST-001");
        customer.setCustomerName("测试客户");
        customer.setStatus("ENABLED");
        return customer;
    }

    private SysRole appRole(Long roleId, String roleKey) {
        SysRole role = new SysRole();
        role.setId(roleId);
        role.setRoleKey(roleKey);
        role.setRoleName("测试角色");
        role.setStatus(1);
        role.setDeleted(0);
        return role;
    }

    private CustomerContact contact(Long contactId, Long customerId, Integer isDefault, EnableStatus status) {
        CustomerContact contact = new CustomerContact();
        contact.setId(contactId);
        contact.setCustomerId(customerId);
        contact.setIsDefault(isDefault);
        contact.setStatus(status);
        return contact;
    }
}
