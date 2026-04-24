package com.supplychain.service.api.customer.dubbo;

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
import com.supplychain.service.api.customer.query.CustomerQuery;
import com.supplychain.service.api.customer.view.CustomerAddressView;
import com.supplychain.service.api.customer.view.CustomerContactView;
import com.supplychain.service.api.customer.view.CustomerDetailView;
import com.supplychain.service.api.customer.view.CustomerView;

import java.util.List;

public interface AdminCustomerDubboService {

    PageResult<CustomerView> listCustomers(SessionUser requester, CustomerQuery query);

    CustomerDetailView getCustomer(SessionUser requester, Long customerId);

    Long createCustomer(CustomerCreateCommand command);

    void updateCustomer(Long customerId, CustomerUpdateCommand command);

    void updateCustomerStatus(Long customerId, String status);

    List<CustomerContactView> listContacts(SessionUser requester, Long customerId);

    Long createContact(Long customerId, CustomerContactCreateCommand command);

    void updateContact(Long customerId, Long contactId, CustomerContactUpdateCommand command);

    void deleteContact(Long customerId, Long contactId);

    List<CustomerAddressView> listAddresses(SessionUser requester, Long customerId);

    Long createAddress(Long customerId, CustomerAddressCreateCommand command);

    void updateAddress(Long customerId, Long addressId, CustomerAddressUpdateCommand command);

    void deleteAddress(Long customerId, Long addressId);

    Long createAppAccount(Long customerId, CustomerAppAccountCreateCommand command);

    void updateAppAccount(Long customerId, Long appUserId, CustomerAppAccountUpdateCommand command);

    void updateAppAccountStatus(Long customerId, Long appUserId, Integer status);

    void resetAppAccountPassword(Long customerId, Long appUserId, String password);
}
