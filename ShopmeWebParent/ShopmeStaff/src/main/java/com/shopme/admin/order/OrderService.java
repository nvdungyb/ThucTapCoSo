package com.shopme.admin.order;

import com.shopme.common.shop.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class OrderService {
    @Autowired
    private OrderRepository orderRepository;

    private final int ORDER_PER_PAGE = 10;

    public Page<Order> listByPage(int pageNum, String keyword) {
        Pageable pageable = PageRequest.of(pageNum - 1, ORDER_PER_PAGE);
        if (keyword != null) {
            return orderRepository.findAll(keyword, pageable);
        }
        return orderRepository.findAll(pageable);
    }

    public void updateCustomerEnabledStatus(Integer id, boolean enabled) {
        orderRepository.updateEnabledStatus(id, enabled);
    }
}
