package com.supplychain.service.provider.customer.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.supplychain.common.core.domain.PageResult;
import com.supplychain.common.core.domain.SessionUser;
import com.supplychain.common.core.enums.CodeEnum;
import com.supplychain.common.core.enums.common.EnableStatus;
import com.supplychain.common.core.enums.customer.CustomerLevel;
import com.supplychain.common.core.enums.customer.CustomerType;
import com.supplychain.common.core.enums.customer.SettlementType;
import com.supplychain.common.core.exception.BizException;
import com.supplychain.service.api.customer.command.CustomerAddressCreateCommand;
import com.supplychain.service.api.customer.command.CustomerAddressUpdateCommand;
import com.supplychain.service.api.customer.command.CustomerAppAccountCreateCommand;
import com.supplychain.service.api.customer.command.CustomerAppAccountUpdateCommand;
import com.supplychain.service.api.customer.command.CustomerContactCreateCommand;
import com.supplychain.service.api.customer.command.CustomerContactUpdateCommand;
import com.supplychain.service.api.customer.command.CustomerCreateCommand;
import com.supplychain.service.api.customer.command.CustomerUpdateCommand;
import com.supplychain.service.api.customer.query.CustomerQuery;
import com.supplychain.service.api.customer.view.CustomerAddressView;
import com.supplychain.service.api.customer.view.CustomerAppAccountView;
import com.supplychain.service.api.customer.view.CustomerContactView;
import com.supplychain.service.api.customer.view.CustomerDetailView;
import com.supplychain.service.api.customer.view.CustomerView;
import com.supplychain.service.provider.app.user.entity.AppUser;
import com.supplychain.service.provider.app.user.mapper.AppUserMapper;
import com.supplychain.service.provider.auth.support.AppPasswordHasher;
import com.supplychain.service.provider.customer.entity.Customer;
import com.supplychain.service.provider.customer.entity.CustomerAddress;
import com.supplychain.service.provider.customer.entity.CustomerContact;
import com.supplychain.service.provider.customer.mapper.CustomerMapper;
import com.supplychain.service.provider.rbac.entity.AppUserRole;
import com.supplychain.service.provider.rbac.entity.SysRole;
import com.supplychain.service.provider.rbac.mapper.AppUserRoleMapper;
import com.supplychain.service.provider.rbac.mapper.SysRoleMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Base64;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomerAdminService {

    private static final String DEFAULT_APP_ROLE_KEY = "app_user";
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private final CustomerMapper customerMapper;
    private final CustomerService customerService;
    private final CustomerContactService customerContactService;
    private final CustomerAddressService customerAddressService;
    private final AppUserMapper appUserMapper;
    private final SysRoleMapper sysRoleMapper;
    private final AppUserRoleMapper appUserRoleMapper;
    private final AppPasswordHasher appPasswordHasher;

    public PageResult<CustomerView> listCustomers(SessionUser requester, CustomerQuery query) {
        LambdaQueryWrapper<Customer> wrapper = Wrappers.lambdaQuery(Customer.class);
        wrapper.eq(Customer::getDeleted, 0);
        if (query != null && StringUtils.hasText(query.getKeyword())) {
            wrapper.and(q -> q.like(Customer::getCustomerCode, query.getKeyword())
                    .or()
                    .like(Customer::getCustomerName, query.getKeyword()));
        }
        if (query != null && StringUtils.hasText(query.getStatus())) {
            wrapper.eq(Customer::getStatus, resolveEnableStatusCode(query.getStatus(), "客户状态"));
        }
        wrapper.orderByAsc(Customer::getId);
        Page<Customer> page = buildPage(query);
        Page<Customer> result = customerMapper.selectPage(page, wrapper);
        return PageResult.<CustomerView>builder()
                .records(toCustomerViews(result.getRecords()))
                .total(result.getTotal())
                .build();
    }

    public CustomerDetailView getCustomer(SessionUser requester, Long customerId) {
        Customer customer = getRequiredCustomer(customerId);
        return CustomerDetailView.builder()
                .customerId(customer.getId())
                .customerCode(customer.getCustomerCode())
                .customerName(customer.getCustomerName())
                .customerType(customer.getCustomerType())
                .customerLevel(customer.getCustomerLevel())
                .salesOwnerId(customer.getSalesOwnerId())
                .status(customer.getStatus())
                .creditEnabled(customer.getCreditEnabled())
                .creditLimit(customer.getCreditLimit())
                .settlementType(customer.getSettlementType())
                .remark(customer.getRemark())
                .contacts(toContactViews(customerContactService.listByCustomerId(customerId)))
                .addresses(toAddressViews(customerAddressService.listByCustomerId(customerId)))
                .appAccounts(toAppAccountViews(listAppUsersByCustomerId(customerId)))
                .build();
    }

    @Transactional(rollbackFor = Exception.class)
    public Long createCustomer(CustomerCreateCommand command) {
        if (command == null) {
            throw new BizException(400, "创建命令不能为空");
        }
        String customerCode = normalizeRequiredText(command.getCustomerCode(), "客户编码");
        assertUniqueCustomerCode(customerCode, null);
        Customer customer = new Customer();
        applyCustomer(customer, command.getCustomerName(), customerCode, command.getCustomerType(), command.getCustomerLevel(),
                command.getSalesOwnerId(), command.getStatus(), command.getCreditEnabled(), command.getCreditLimit(),
                command.getSettlementType(), command.getRemark());
        if (customerMapper.insert(customer) != 1) {
            throw new BizException(500, "客户创建失败");
        }
        return customer.getId();
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateCustomer(Long customerId, CustomerUpdateCommand command) {
        validatePositiveId(customerId, "客户ID");
        if (command == null) {
            throw new BizException(400, "更新命令不能为空");
        }
        Customer customer = getRequiredCustomer(customerId);
        String customerCode = normalizeRequiredText(command.getCustomerCode(), "客户编码");
        assertUniqueCustomerCode(customerCode, customerId);
        applyCustomer(customer, command.getCustomerName(), customerCode, command.getCustomerType(), command.getCustomerLevel(),
                command.getSalesOwnerId(), command.getStatus(), command.getCreditEnabled(), command.getCreditLimit(),
                command.getSettlementType(), command.getRemark());
        if (customerMapper.updateById(customer) != 1) {
            throw new BizException(500, "客户更新失败");
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateCustomerStatus(Long customerId, String status) {
        Customer customer = getRequiredCustomer(customerId);
        customer.setStatus(resolveEnableStatusCode(status, "客户状态"));
        if (customerMapper.updateById(customer) != 1) {
            throw new BizException(500, "客户状态更新失败");
        }
    }

    public List<CustomerContactView> listContacts(SessionUser requester, Long customerId) {
        ensureCustomerExists(customerId);
        return toContactViews(customerContactService.listByCustomerId(customerId));
    }

    @Transactional(rollbackFor = Exception.class)
    public Long createContact(Long customerId, CustomerContactCreateCommand command) {
        ensureCustomerExists(customerId);
        if (command == null) {
            throw new BizException(400, "创建命令不能为空");
        }
        CustomerContact contact = new CustomerContact();
        contact.setCustomerId(customerId);
        applyContact(contact, command.getContactName(), command.getMobile(), command.getPhone(), command.getEmail(),
                command.getIsDefault(), command.getStatus(), command.getRemark());
        Long contactId = customerContactService.create(contact);
        if (Objects.equals(contact.getIsDefault(), 1)) {
            clearOtherDefaultContacts(customerId, contactId);
        }
        return contactId;
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateContact(Long customerId, Long contactId, CustomerContactUpdateCommand command) {
        ensureCustomerExists(customerId);
        if (command == null) {
            throw new BizException(400, "更新命令不能为空");
        }
        CustomerContact contact = getRequiredContact(customerId, contactId);
        applyContact(contact, command.getContactName(), command.getMobile(), command.getPhone(), command.getEmail(),
                command.getIsDefault() == null ? contact.getIsDefault() : command.getIsDefault(),
                command.getStatus() == null || command.getStatus().isBlank()
                        ? contact.getStatus() == null ? null : contact.getStatus().getCode()
                        : command.getStatus(),
                command.getRemark());
        customerContactService.update(contact);
        if (Objects.equals(contact.getIsDefault(), 1)) {
            clearOtherDefaultContacts(customerId, contactId);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteContact(Long customerId, Long contactId) {
        ensureCustomerExists(customerId);
        getRequiredContact(customerId, contactId);
        customerContactService.delete(contactId);
    }

    public List<CustomerAddressView> listAddresses(SessionUser requester, Long customerId) {
        ensureCustomerExists(customerId);
        return toAddressViews(customerAddressService.listByCustomerId(customerId));
    }

    @Transactional(rollbackFor = Exception.class)
    public Long createAddress(Long customerId, CustomerAddressCreateCommand command) {
        ensureCustomerExists(customerId);
        if (command == null) {
            throw new BizException(400, "创建命令不能为空");
        }
        CustomerAddress address = new CustomerAddress();
        address.setCustomerId(customerId);
        applyAddress(address, command.getReceiverName(), command.getReceiverPhone(), command.getProvinceCode(),
                command.getCityCode(), command.getDistrictCode(), command.getDetailAddress(), command.getIsDefault(),
                command.getStatus(), command.getRemark());
        Long addressId = customerAddressService.create(address);
        if (Objects.equals(address.getIsDefault(), 1)) {
            clearOtherDefaultAddresses(customerId, addressId);
        }
        return addressId;
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateAddress(Long customerId, Long addressId, CustomerAddressUpdateCommand command) {
        ensureCustomerExists(customerId);
        if (command == null) {
            throw new BizException(400, "更新命令不能为空");
        }
        CustomerAddress address = getRequiredAddress(customerId, addressId);
        applyAddress(address, command.getReceiverName(), command.getReceiverPhone(), command.getProvinceCode(),
                command.getCityCode(), command.getDistrictCode(), command.getDetailAddress(),
                command.getIsDefault() == null ? address.getIsDefault() : command.getIsDefault(),
                command.getStatus() == null || command.getStatus().isBlank()
                        ? address.getStatus() == null ? null : address.getStatus().getCode()
                        : command.getStatus(),
                command.getRemark());
        customerAddressService.update(address);
        if (Objects.equals(address.getIsDefault(), 1)) {
            clearOtherDefaultAddresses(customerId, addressId);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteAddress(Long customerId, Long addressId) {
        ensureCustomerExists(customerId);
        getRequiredAddress(customerId, addressId);
        customerAddressService.delete(addressId);
    }

    @Transactional(rollbackFor = Exception.class)
    public Long createAppAccount(Long customerId, CustomerAppAccountCreateCommand command) {
        ensureCustomerExists(customerId);
        if (command == null) {
            throw new BizException(400, "创建命令不能为空");
        }
        String username = normalizeRequiredText(command.getUsername(), "账号");
        assertUniqueAppUsername(username, null);
        AppUser user = new AppUser();
        user.setCustomerId(customerId);
        user.setUsername(username);
        user.setDisplayName(normalizeRequiredText(command.getDisplayName(), "显示名称"));
        user.setStatus(resolveBinaryStatus(command.getStatus(), "账号状态"));
        applyPassword(user, command.getPassword());
        if (appUserMapper.insert(user) != 1) {
            throw new BizException(500, "客户 App 账号创建失败");
        }
        syncAppUserRoles(user.getId(), command.getRoleIds());
        return user.getId();
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateAppAccount(Long customerId, Long appUserId, CustomerAppAccountUpdateCommand command) {
        ensureCustomerExists(customerId);
        if (command == null) {
            throw new BizException(400, "更新命令不能为空");
        }
        AppUser user = getRequiredAppUser(customerId, appUserId);
        String username = normalizeRequiredText(command.getUsername(), "账号");
        assertUniqueAppUsername(username, appUserId);
        user.setUsername(username);
        user.setDisplayName(normalizeRequiredText(command.getDisplayName(), "显示名称"));
        user.setStatus(command.getStatus() == null ? user.getStatus() : resolveBinaryStatus(command.getStatus(), "账号状态"));
        if (appUserMapper.updateById(user) != 1) {
            throw new BizException(500, "客户 App 账号更新失败");
        }
        syncAppUserRoles(appUserId, command.getRoleIds() == null ? currentRoleIds(appUserId) : command.getRoleIds());
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateAppAccountStatus(Long customerId, Long appUserId, Integer status) {
        ensureCustomerExists(customerId);
        AppUser user = getRequiredAppUser(customerId, appUserId);
        user.setStatus(resolveBinaryStatus(status, "账号状态"));
        if (appUserMapper.updateById(user) != 1) {
            throw new BizException(500, "客户 App 账号状态更新失败");
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void resetAppAccountPassword(Long customerId, Long appUserId, String password) {
        ensureCustomerExists(customerId);
        AppUser user = getRequiredAppUser(customerId, appUserId);
        applyPassword(user, password);
        if (appUserMapper.updateById(user) != 1) {
            throw new BizException(500, "客户 App 账号密码重置失败");
        }
    }

    private List<CustomerView> toCustomerViews(List<Customer> customers) {
        if (CollectionUtils.isEmpty(customers)) {
            return List.of();
        }
        List<Long> customerIds = customers.stream().map(Customer::getId).toList();
        Map<Long, Long> contactCountMap = customerContactService.listByCustomerIds(customerIds).stream()
                .collect(Collectors.groupingBy(CustomerContact::getCustomerId, Collectors.counting()));
        Map<Long, Long> addressCountMap = customerAddressService.listByCustomerIds(customerIds).stream()
                .collect(Collectors.groupingBy(CustomerAddress::getCustomerId, Collectors.counting()));
        Map<Long, List<AppUser>> appUserMap = listAppUsersByCustomerIds(customerIds).stream()
                .collect(Collectors.groupingBy(AppUser::getCustomerId));
        return customers.stream().map(customer -> {
            List<AppUser> appUsers = appUserMap.getOrDefault(customer.getId(), List.of());
            long enabledAppAccountCount = appUsers.stream()
                    .filter(appUser -> Objects.equals(appUser.getStatus(), 1))
                    .count();
            return CustomerView.builder()
                    .customerId(customer.getId())
                    .customerCode(customer.getCustomerCode())
                    .customerName(customer.getCustomerName())
                    .customerType(customer.getCustomerType())
                    .customerLevel(customer.getCustomerLevel())
                    .salesOwnerId(customer.getSalesOwnerId())
                    .status(customer.getStatus())
                    .creditEnabled(customer.getCreditEnabled())
                    .creditLimit(customer.getCreditLimit())
                    .settlementType(customer.getSettlementType())
                    .remark(customer.getRemark())
                    .contactCount(contactCountMap.getOrDefault(customer.getId(), 0L).intValue())
                    .addressCount(addressCountMap.getOrDefault(customer.getId(), 0L).intValue())
                    .appAccountCount(appUsers.size())
                    .enabledAppAccountCount((int) enabledAppAccountCount)
                    .build();
        }).toList();
    }

    private List<CustomerContactView> toContactViews(List<CustomerContact> contacts) {
        return contacts.stream().map(contact -> CustomerContactView.builder()
                .contactId(contact.getId())
                .customerId(contact.getCustomerId())
                .contactName(contact.getContactName())
                .mobile(contact.getMobile())
                .phone(contact.getPhone())
                .email(contact.getEmail())
                .isDefault(contact.getIsDefault())
                .status(contact.getStatus() == null ? null : contact.getStatus().getCode())
                .remark(contact.getRemark())
                .build()).toList();
    }

    private List<CustomerAddressView> toAddressViews(List<CustomerAddress> addresses) {
        return addresses.stream().map(address -> CustomerAddressView.builder()
                .addressId(address.getId())
                .customerId(address.getCustomerId())
                .receiverName(address.getReceiverName())
                .receiverPhone(address.getReceiverPhone())
                .provinceCode(address.getProvinceCode())
                .cityCode(address.getCityCode())
                .districtCode(address.getDistrictCode())
                .detailAddress(address.getDetailAddress())
                .isDefault(address.getIsDefault())
                .status(address.getStatus() == null ? null : address.getStatus().getCode())
                .remark(address.getRemark())
                .build()).toList();
    }

    private List<CustomerAppAccountView> toAppAccountViews(List<AppUser> appUsers) {
        if (CollectionUtils.isEmpty(appUsers)) {
            return List.of();
        }
        List<Long> userIds = appUsers.stream().map(AppUser::getId).toList();
        List<AppUserRole> relations = appUserRoleMapper.selectList(Wrappers.lambdaQuery(AppUserRole.class)
                .in(AppUserRole::getUserId, userIds));
        Map<Long, List<AppUserRole>> relationMap = relations.stream()
                .collect(Collectors.groupingBy(AppUserRole::getUserId));
        List<Long> roleIds = relations.stream()
                .map(AppUserRole::getRoleId)
                .filter(Objects::nonNull)
                .distinct()
                .toList();
        Map<Long, SysRole> roleMap = CollectionUtils.isEmpty(roleIds) ? Map.of()
                : sysRoleMapper.selectByIds(roleIds).stream()
                .filter(Objects::nonNull)
                .filter(role -> role.getDeleted() == null || role.getDeleted() == 0)
                .collect(Collectors.toMap(SysRole::getId, Function.identity()));
        return appUsers.stream().map(appUser -> {
            List<SysRole> roles = relationMap.getOrDefault(appUser.getId(), List.of()).stream()
                    .map(item -> roleMap.get(item.getRoleId()))
                    .filter(Objects::nonNull)
                    .sorted((left, right) -> Long.compare(left.getId(), right.getId()))
                    .toList();
            return CustomerAppAccountView.builder()
                    .userId(appUser.getId())
                    .customerId(appUser.getCustomerId())
                    .username(appUser.getUsername())
                    .displayName(appUser.getDisplayName())
                    .status(appUser.getStatus())
                    .roleIds(roles.stream().map(SysRole::getId).toList())
                    .roleKeys(roles.stream().map(SysRole::getRoleKey).toList())
                    .roleNames(roles.stream().map(SysRole::getRoleName).toList())
                    .build();
        }).toList();
    }

    private void applyCustomer(Customer customer, String customerName, String customerCode, String customerType,
                               String customerLevel, Long salesOwnerId, String status, Integer creditEnabled,
                               BigDecimal creditLimit, String settlementType, String remark) {
        customer.setCustomerCode(customerCode);
        customer.setCustomerName(normalizeRequiredText(customerName, "客户名称"));
        customer.setCustomerType(resolveCode(customerType, CustomerType.class, CustomerType.ENTERPRISE.getCode(), "客户类型"));
        customer.setCustomerLevel(resolveCode(customerLevel, CustomerLevel.class, CustomerLevel.NORMAL.getCode(), "客户等级"));
        customer.setSalesOwnerId(salesOwnerId);
        customer.setStatus(resolveEnableStatusCode(status, "客户状态"));
        customer.setCreditEnabled(resolveBinaryFlag(creditEnabled, "授信开关"));
        customer.setCreditLimit(resolveCreditLimit(creditLimit));
        customer.setSettlementType(resolveCode(settlementType, SettlementType.class, SettlementType.CASH.getCode(), "结算方式"));
        customer.setRemark(normalizeOptionalText(remark));
    }

    private void applyContact(CustomerContact contact, String contactName, String mobile, String phone, String email,
                              Integer isDefault, String status, String remark) {
        contact.setContactName(normalizeRequiredText(contactName, "联系人姓名"));
        contact.setMobile(normalizeOptionalText(mobile));
        contact.setPhone(normalizeOptionalText(phone));
        contact.setEmail(normalizeOptionalText(email));
        contact.setIsDefault(resolveDefaultFlag(contact.getCustomerId(), isDefault, true));
        contact.setStatus(EnableStatus.fromCode(resolveEnableStatusCode(status, "联系人状态")));
        contact.setRemark(normalizeOptionalText(remark));
    }

    private void applyAddress(CustomerAddress address, String receiverName, String receiverPhone, String provinceCode,
                              String cityCode, String districtCode, String detailAddress, Integer isDefault,
                              String status, String remark) {
        address.setReceiverName(normalizeRequiredText(receiverName, "收货人姓名"));
        address.setReceiverPhone(normalizeRequiredText(receiverPhone, "收货人电话"));
        address.setProvinceCode(normalizeOptionalText(provinceCode));
        address.setCityCode(normalizeOptionalText(cityCode));
        address.setDistrictCode(normalizeOptionalText(districtCode));
        address.setDetailAddress(normalizeRequiredText(detailAddress, "详细地址"));
        address.setIsDefault(resolveDefaultFlag(address.getCustomerId(), isDefault, false));
        address.setStatus(EnableStatus.fromCode(resolveEnableStatusCode(status, "地址状态")));
        address.setRemark(normalizeOptionalText(remark));
    }

    private Integer resolveDefaultFlag(Long customerId, Integer isDefault, boolean contact) {
        if (isDefault == null) {
            boolean exists = contact ? !CollectionUtils.isEmpty(customerContactService.listByCustomerId(customerId))
                    : !CollectionUtils.isEmpty(customerAddressService.listByCustomerId(customerId));
            return exists ? 0 : 1;
        }
        return resolveBinaryFlag(isDefault, "默认标记");
    }

    private Integer resolveBinaryStatus(Integer status, String fieldName) {
        if (status == null) {
            return 1;
        }
        if (!Objects.equals(status, 0) && !Objects.equals(status, 1)) {
            throw new BizException(400, fieldName + "不合法");
        }
        return status;
    }

    private Integer resolveBinaryFlag(Integer value, String fieldName) {
        if (value == null) {
            return 0;
        }
        if (!Objects.equals(value, 0) && !Objects.equals(value, 1)) {
            throw new BizException(400, fieldName + "不合法");
        }
        return value;
    }

    private BigDecimal resolveCreditLimit(BigDecimal creditLimit) {
        if (creditLimit == null) {
            return BigDecimal.ZERO;
        }
        if (creditLimit.signum() < 0) {
            throw new BizException(400, "授信额度不能小于 0");
        }
        return creditLimit;
    }

    private String resolveEnableStatusCode(String status, String fieldName) {
        return resolveCode(status, EnableStatus.class, EnableStatus.ENABLED.getCode(), fieldName);
    }

    private <E extends Enum<E> & CodeEnum> String resolveCode(String code, Class<E> enumType,
                                                              String defaultValue, String fieldName) {
        String value = StringUtils.hasText(code) ? code.trim() : defaultValue;
        E enumValue = CodeEnum.fromCode(enumType, value);
        if (enumValue == null) {
            throw new BizException(400, fieldName + "不合法");
        }
        return enumValue.getCode();
    }

    private void applyPassword(AppUser user, String rawPassword) {
        String password = normalizeRequiredText(rawPassword, "密码");
        String salt = randomSalt();
        user.setPasswordSalt(salt);
        user.setPasswordHash(appPasswordHasher.encode(password, salt));
    }

    private String randomSalt() {
        byte[] bytes = new byte[16];
        SECURE_RANDOM.nextBytes(bytes);
        return Base64.getEncoder().encodeToString(bytes);
    }

    private void syncAppUserRoles(Long userId, List<Long> roleIds) {
        validatePositiveId(userId, "用户ID");
        List<Long> normalizedRoleIds = normalizeIds(roleIds, "角色ID");
        if (CollectionUtils.isEmpty(normalizedRoleIds)) {
            normalizedRoleIds = List.of(resolveDefaultAppRoleId());
        }
        ensureRolesExist(normalizedRoleIds);
        appUserRoleMapper.delete(Wrappers.lambdaQuery(AppUserRole.class)
                .eq(AppUserRole::getUserId, userId));
        for (Long roleId : normalizedRoleIds) {
            AppUserRole relation = new AppUserRole();
            relation.setUserId(userId);
            relation.setRoleId(roleId);
            if (appUserRoleMapper.insert(relation) != 1) {
                throw new BizException(500, "客户 App 账号角色关联保存失败");
            }
        }
    }

    private Long resolveDefaultAppRoleId() {
        SysRole role = sysRoleMapper.selectByRoleKey(DEFAULT_APP_ROLE_KEY);
        if (role == null || role.getDeleted() != null && role.getDeleted() == 1 || !Objects.equals(role.getStatus(), 1)) {
            throw new BizException(500, "默认 App 角色不存在");
        }
        return role.getId();
    }

    private List<Long> currentRoleIds(Long userId) {
        return appUserRoleMapper.selectList(Wrappers.lambdaQuery(AppUserRole.class)
                        .eq(AppUserRole::getUserId, userId))
                .stream()
                .map(AppUserRole::getRoleId)
                .filter(Objects::nonNull)
                .toList();
    }

    private void ensureRolesExist(List<Long> roleIds) {
        List<SysRole> roles = sysRoleMapper.selectByIds(roleIds).stream()
                .filter(Objects::nonNull)
                .filter(role -> role.getDeleted() == null || role.getDeleted() == 0)
                .filter(role -> Objects.equals(role.getStatus(), 1))
                .toList();
        if (roles.size() != roleIds.size()) {
            throw new BizException(400, "存在无效角色");
        }
    }

    private void assertUniqueCustomerCode(String customerCode, Long excludeCustomerId) {
        Customer existing = customerMapper.selectByCustomerCode(customerCode);
        if (existing != null && !Objects.equals(existing.getId(), excludeCustomerId)) {
            throw new BizException(400, "客户编码已存在");
        }
    }

    private void assertUniqueAppUsername(String username, Long excludeUserId) {
        AppUser existing = appUserMapper.selectByUsername(username);
        if (existing != null && !Objects.equals(existing.getId(), excludeUserId)) {
            throw new BizException(400, "App 账号已存在");
        }
    }

    private List<AppUser> listAppUsersByCustomerId(Long customerId) {
        return appUserMapper.selectList(Wrappers.lambdaQuery(AppUser.class)
                .eq(AppUser::getCustomerId, customerId)
                .eq(AppUser::getDeleted, 0)
                .orderByAsc(AppUser::getId));
    }

    private List<AppUser> listAppUsersByCustomerIds(List<Long> customerIds) {
        if (CollectionUtils.isEmpty(customerIds)) {
            return List.of();
        }
        return appUserMapper.selectList(Wrappers.lambdaQuery(AppUser.class)
                .in(AppUser::getCustomerId, customerIds)
                .eq(AppUser::getDeleted, 0)
                .orderByAsc(AppUser::getId));
    }

    private AppUser getRequiredAppUser(Long customerId, Long appUserId) {
        validatePositiveId(appUserId, "App 用户ID");
        AppUser user = appUserMapper.selectByUserId(appUserId);
        if (user == null || !Objects.equals(user.getCustomerId(), customerId)) {
            throw new BizException(404, "客户 App 账号不存在");
        }
        return user;
    }

    private Customer getRequiredCustomer(Long customerId) {
        Customer customer = customerService.getById(customerId);
        if (customer == null) {
            throw new BizException(404, "客户不存在");
        }
        return customer;
    }

    private Page<Customer> buildPage(CustomerQuery query) {
        long pageNum = query == null || query.getPageNum() == null ? 1L : query.getPageNum();
        long pageSize = query == null || query.getPageSize() == null ? 20L : query.getPageSize();
        if (pageNum <= 0 || pageSize <= 0) {
            throw new BizException(400, "分页参数不合法");
        }
        return new Page<>(pageNum, pageSize);
    }

    private CustomerContact getRequiredContact(Long customerId, Long contactId) {
        CustomerContact contact = customerContactService.getById(contactId);
        if (contact == null || !Objects.equals(contact.getCustomerId(), customerId)) {
            throw new BizException(404, "客户联系人不存在");
        }
        return contact;
    }

    private CustomerAddress getRequiredAddress(Long customerId, Long addressId) {
        CustomerAddress address = customerAddressService.getById(addressId);
        if (address == null || !Objects.equals(address.getCustomerId(), customerId)) {
            throw new BizException(404, "客户地址不存在");
        }
        return address;
    }

    private void clearOtherDefaultContacts(Long customerId, Long keepContactId) {
        List<CustomerContact> contacts = customerContactService.listByCustomerId(customerId);
        for (CustomerContact item : contacts) {
            if (Objects.equals(item.getId(), keepContactId) || !Objects.equals(item.getIsDefault(), 1)) {
                continue;
            }
            item.setIsDefault(0);
            customerContactService.update(item);
        }
    }

    private void clearOtherDefaultAddresses(Long customerId, Long keepAddressId) {
        List<CustomerAddress> addresses = customerAddressService.listByCustomerId(customerId);
        for (CustomerAddress item : addresses) {
            if (Objects.equals(item.getId(), keepAddressId) || !Objects.equals(item.getIsDefault(), 1)) {
                continue;
            }
            item.setIsDefault(0);
            customerAddressService.update(item);
        }
    }

    private void ensureCustomerExists(Long customerId) {
        getRequiredCustomer(customerId);
    }

    private List<Long> normalizeIds(List<Long> ids, String fieldName) {
        if (CollectionUtils.isEmpty(ids)) {
            return List.of();
        }
        LinkedHashSet<Long> values = new LinkedHashSet<>();
        for (Long id : ids) {
            if (id == null || id <= 0) {
                throw new BizException(400, fieldName + "不能为空");
            }
            values.add(id);
        }
        return new ArrayList<>(values);
    }

    private void validatePositiveId(Long id, String fieldName) {
        if (id == null || id <= 0) {
            throw new BizException(400, fieldName + "不能为空");
        }
    }

    private String normalizeRequiredText(String text, String fieldName) {
        String value = normalizeOptionalText(text);
        if (!StringUtils.hasText(value)) {
            throw new BizException(400, fieldName + "不能为空");
        }
        return value;
    }

    private String normalizeOptionalText(String text) {
        return StringUtils.hasText(text) ? text.trim() : null;
    }
}
