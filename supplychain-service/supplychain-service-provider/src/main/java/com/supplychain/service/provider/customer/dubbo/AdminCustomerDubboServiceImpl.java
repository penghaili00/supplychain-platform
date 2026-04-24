package com.supplychain.service.provider.customer.dubbo;

import com.supplychain.common.core.domain.PageResult;
import com.supplychain.common.core.domain.SessionUser;
import com.supplychain.service.api.customer.command.CustomerAddressCreateCommand;
import com.supplychain.service.api.customer.command.CustomerAddressUpdateCommand;
import com.supplychain.service.api.customer.command.CustomerAppAccountCreateCommand;
import com.supplychain.service.api.customer.command.CustomerAppAccountUpdateCommand;
import com.supplychain.service.api.customer.command.CustomerContactCreateCommand;
import com.supplychain.service.api.customer.command.CustomerContactUpdateCommand;
import com.supplychain.service.api.customer.command.CustomerCreateCommand;
import com.supplychain.service.api.customer.command.CustomerUpdateCommand;
import com.supplychain.service.api.customer.dubbo.AdminCustomerDubboService;
import com.supplychain.service.api.customer.query.CustomerQuery;
import com.supplychain.service.api.customer.view.CustomerAddressView;
import com.supplychain.service.api.customer.view.CustomerContactView;
import com.supplychain.service.api.customer.view.CustomerDetailView;
import com.supplychain.service.api.customer.view.CustomerView;
import com.supplychain.service.provider.customer.service.CustomerAdminService;
import lombok.RequiredArgsConstructor;
import org.apache.dubbo.config.annotation.DubboService;

import java.util.List;

@DubboService
@RequiredArgsConstructor
public class AdminCustomerDubboServiceImpl implements AdminCustomerDubboService {

    private final CustomerAdminService customerAdminService;

    @Override
    public PageResult<CustomerView> listCustomers(SessionUser requester, CustomerQuery query) {
        return customerAdminService.listCustomers(requester, query);
    }

    @Override
    public CustomerDetailView getCustomer(SessionUser requester, Long customerId) {
        return customerAdminService.getCustomer(requester, customerId);
    }

    @Override
    public Long createCustomer(CustomerCreateCommand command) {
        return customerAdminService.createCustomer(command);
    }

    @Override
    public void updateCustomer(Long customerId, CustomerUpdateCommand command) {
        customerAdminService.updateCustomer(customerId, command);
    }

    @Override
    public void updateCustomerStatus(Long customerId, String status) {
        customerAdminService.updateCustomerStatus(customerId, status);
    }

    @Override
    public List<CustomerContactView> listContacts(SessionUser requester, Long customerId) {
        return customerAdminService.listContacts(requester, customerId);
    }

    @Override
    public Long createContact(Long customerId, CustomerContactCreateCommand command) {
        return customerAdminService.createContact(customerId, command);
    }

    @Override
    public void updateContact(Long customerId, Long contactId, CustomerContactUpdateCommand command) {
        customerAdminService.updateContact(customerId, contactId, command);
    }

    @Override
    public void deleteContact(Long customerId, Long contactId) {
        customerAdminService.deleteContact(customerId, contactId);
    }

    @Override
    public List<CustomerAddressView> listAddresses(SessionUser requester, Long customerId) {
        return customerAdminService.listAddresses(requester, customerId);
    }

    @Override
    public Long createAddress(Long customerId, CustomerAddressCreateCommand command) {
        return customerAdminService.createAddress(customerId, command);
    }

    @Override
    public void updateAddress(Long customerId, Long addressId, CustomerAddressUpdateCommand command) {
        customerAdminService.updateAddress(customerId, addressId, command);
    }

    @Override
    public void deleteAddress(Long customerId, Long addressId) {
        customerAdminService.deleteAddress(customerId, addressId);
    }

    @Override
    public Long createAppAccount(Long customerId, CustomerAppAccountCreateCommand command) {
        return customerAdminService.createAppAccount(customerId, command);
    }

    @Override
    public void updateAppAccount(Long customerId, Long appUserId, CustomerAppAccountUpdateCommand command) {
        customerAdminService.updateAppAccount(customerId, appUserId, command);
    }

    @Override
    public void updateAppAccountStatus(Long customerId, Long appUserId, Integer status) {
        customerAdminService.updateAppAccountStatus(customerId, appUserId, status);
    }

    @Override
    public void resetAppAccountPassword(Long customerId, Long appUserId, String password) {
        customerAdminService.resetAppAccountPassword(customerId, appUserId, password);
    }
}
