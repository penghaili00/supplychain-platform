package com.supplychain.admin.controller;

import com.supplychain.common.core.annotation.OperationLog;
import com.supplychain.common.core.annotation.RequirePermission;
import com.supplychain.common.core.domain.PageResult;
import com.supplychain.common.core.domain.R;
import com.supplychain.common.core.domain.SessionUser;
import com.supplychain.common.core.enums.OperationType;
import com.supplychain.common.core.exception.UnauthorizedException;
import com.supplychain.common.security.context.UserContextHolder;
import com.supplychain.service.api.customer.command.CustomerAddressCreateCommand;
import com.supplychain.service.api.customer.command.CustomerAddressUpdateCommand;
import com.supplychain.service.api.customer.command.CustomerAppAccountCreateCommand;
import com.supplychain.service.api.customer.command.CustomerAppAccountPasswordCommand;
import com.supplychain.service.api.customer.command.CustomerAppAccountStatusCommand;
import com.supplychain.service.api.customer.command.CustomerAppAccountUpdateCommand;
import com.supplychain.service.api.customer.command.CustomerContactCreateCommand;
import com.supplychain.service.api.customer.command.CustomerContactUpdateCommand;
import com.supplychain.service.api.customer.command.CustomerCreateCommand;
import com.supplychain.service.api.customer.command.CustomerStatusCommand;
import com.supplychain.service.api.customer.command.CustomerUpdateCommand;
import com.supplychain.service.api.customer.dubbo.AdminCustomerDubboService;
import com.supplychain.service.api.customer.query.CustomerQuery;
import com.supplychain.service.api.customer.view.CustomerAddressView;
import com.supplychain.service.api.customer.view.CustomerContactView;
import com.supplychain.service.api.customer.view.CustomerDetailView;
import com.supplychain.service.api.customer.view.CustomerView;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class AdminCustomerController {

    @DubboReference(check = false)
    private AdminCustomerDubboService adminCustomerDubboService;

    @GetMapping("/admin/customers")
    @RequirePermission("customer:customer:list")
    @OperationLog(title = "查询客户列表", businessType = OperationType.QUERY)
    public R<PageResult<CustomerView>> list(CustomerQuery query) {
        return R.ok(adminCustomerDubboService.listCustomers(currentUser(), query));
    }

    @GetMapping("/admin/customers/{customerId}")
    @RequirePermission("customer:customer:view")
    @OperationLog(title = "查询客户详情", businessType = OperationType.QUERY)
    public R<CustomerDetailView> detail(@PathVariable Long customerId) {
        return R.ok(adminCustomerDubboService.getCustomer(currentUser(), customerId));
    }

    @PostMapping("/admin/customers")
    @RequirePermission("customer:customer:create")
    @OperationLog(title = "创建客户", businessType = OperationType.CREATE)
    public R<Long> create(@Valid @RequestBody CustomerCreateCommand command) {
        return R.ok(adminCustomerDubboService.createCustomer(command));
    }

    @PutMapping("/admin/customers/{customerId}")
    @RequirePermission("customer:customer:update")
    @OperationLog(title = "更新客户", businessType = OperationType.UPDATE)
    public R<Void> update(@PathVariable Long customerId, @Valid @RequestBody CustomerUpdateCommand command) {
        adminCustomerDubboService.updateCustomer(customerId, command);
        return R.ok(null);
    }

    @PutMapping("/admin/customers/{customerId}/status")
    @RequirePermission("customer:customer:update")
    @OperationLog(title = "更新客户状态", businessType = OperationType.UPDATE)
    public R<Void> updateStatus(@PathVariable Long customerId, @Valid @RequestBody CustomerStatusCommand command) {
        adminCustomerDubboService.updateCustomerStatus(customerId, command.getStatus());
        return R.ok(null);
    }

    @GetMapping("/admin/customers/{customerId}/contacts")
    @RequirePermission("customer:contact:list")
    @OperationLog(title = "查询客户联系人", businessType = OperationType.QUERY)
    public R<List<CustomerContactView>> listContacts(@PathVariable Long customerId) {
        return R.ok(adminCustomerDubboService.listContacts(currentUser(), customerId));
    }

    @PostMapping("/admin/customers/{customerId}/contacts")
    @RequirePermission("customer:contact:create")
    @OperationLog(title = "创建客户联系人", businessType = OperationType.CREATE)
    public R<Long> createContact(@PathVariable Long customerId,
                                 @Valid @RequestBody CustomerContactCreateCommand command) {
        return R.ok(adminCustomerDubboService.createContact(customerId, command));
    }

    @PutMapping("/admin/customers/{customerId}/contacts/{contactId}")
    @RequirePermission("customer:contact:update")
    @OperationLog(title = "更新客户联系人", businessType = OperationType.UPDATE)
    public R<Void> updateContact(@PathVariable Long customerId,
                                 @PathVariable Long contactId,
                                 @Valid @RequestBody CustomerContactUpdateCommand command) {
        adminCustomerDubboService.updateContact(customerId, contactId, command);
        return R.ok(null);
    }

    @DeleteMapping("/admin/customers/{customerId}/contacts/{contactId}")
    @RequirePermission("customer:contact:delete")
    @OperationLog(title = "删除客户联系人", businessType = OperationType.DELETE)
    public R<Void> deleteContact(@PathVariable Long customerId, @PathVariable Long contactId) {
        adminCustomerDubboService.deleteContact(customerId, contactId);
        return R.ok(null);
    }

    @GetMapping("/admin/customers/{customerId}/addresses")
    @RequirePermission("customer:address:list")
    @OperationLog(title = "查询客户地址", businessType = OperationType.QUERY)
    public R<List<CustomerAddressView>> listAddresses(@PathVariable Long customerId) {
        return R.ok(adminCustomerDubboService.listAddresses(currentUser(), customerId));
    }

    @PostMapping("/admin/customers/{customerId}/addresses")
    @RequirePermission("customer:address:create")
    @OperationLog(title = "创建客户地址", businessType = OperationType.CREATE)
    public R<Long> createAddress(@PathVariable Long customerId,
                                 @Valid @RequestBody CustomerAddressCreateCommand command) {
        return R.ok(adminCustomerDubboService.createAddress(customerId, command));
    }

    @PutMapping("/admin/customers/{customerId}/addresses/{addressId}")
    @RequirePermission("customer:address:update")
    @OperationLog(title = "更新客户地址", businessType = OperationType.UPDATE)
    public R<Void> updateAddress(@PathVariable Long customerId,
                                 @PathVariable Long addressId,
                                 @Valid @RequestBody CustomerAddressUpdateCommand command) {
        adminCustomerDubboService.updateAddress(customerId, addressId, command);
        return R.ok(null);
    }

    @DeleteMapping("/admin/customers/{customerId}/addresses/{addressId}")
    @RequirePermission("customer:address:delete")
    @OperationLog(title = "删除客户地址", businessType = OperationType.DELETE)
    public R<Void> deleteAddress(@PathVariable Long customerId, @PathVariable Long addressId) {
        adminCustomerDubboService.deleteAddress(customerId, addressId);
        return R.ok(null);
    }

    @PostMapping("/admin/customers/{customerId}/app-accounts")
    @RequirePermission("customer:app-account:create")
    @OperationLog(title = "开通客户 App 账号", businessType = OperationType.CREATE)
    public R<Long> createAppAccount(@PathVariable Long customerId,
                                    @Valid @RequestBody CustomerAppAccountCreateCommand command) {
        return R.ok(adminCustomerDubboService.createAppAccount(customerId, command));
    }

    @PutMapping("/admin/customers/{customerId}/app-accounts/{appUserId}")
    @RequirePermission("customer:app-account:update")
    @OperationLog(title = "更新客户 App 账号", businessType = OperationType.UPDATE)
    public R<Void> updateAppAccount(@PathVariable Long customerId,
                                    @PathVariable Long appUserId,
                                    @Valid @RequestBody CustomerAppAccountUpdateCommand command) {
        adminCustomerDubboService.updateAppAccount(customerId, appUserId, command);
        return R.ok(null);
    }

    @PutMapping("/admin/customers/{customerId}/app-accounts/{appUserId}/status")
    @RequirePermission("customer:app-account:update")
    @OperationLog(title = "更新客户 App 账号状态", businessType = OperationType.UPDATE)
    public R<Void> updateAppAccountStatus(@PathVariable Long customerId,
                                          @PathVariable Long appUserId,
                                          @Valid @RequestBody CustomerAppAccountStatusCommand command) {
        adminCustomerDubboService.updateAppAccountStatus(customerId, appUserId, command.getStatus());
        return R.ok(null);
    }

    @PutMapping("/admin/customers/{customerId}/app-accounts/{appUserId}/password")
    @RequirePermission("customer:app-account:reset-password")
    @OperationLog(title = "重置客户 App 账号密码", businessType = OperationType.UPDATE)
    public R<Void> resetAppAccountPassword(@PathVariable Long customerId,
                                           @PathVariable Long appUserId,
                                           @Valid @RequestBody CustomerAppAccountPasswordCommand command) {
        adminCustomerDubboService.resetAppAccountPassword(customerId, appUserId, command.getPassword());
        return R.ok(null);
    }

    private SessionUser currentUser() {
        SessionUser sessionUser = UserContextHolder.getUser();
        if (sessionUser == null) {
            throw new UnauthorizedException("未登录");
        }
        return sessionUser;
    }
}
