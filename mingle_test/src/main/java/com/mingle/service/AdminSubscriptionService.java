package com.mingle.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mingle.dto.AdminProductRequest;
import com.mingle.mapper.SubscriptionMapper;
import com.mingle.vo.ProductVO;

@Service
public class AdminSubscriptionService {

    @Autowired
    private SubscriptionMapper subscriptionMapper;

    @Transactional(readOnly = true)
    public List<ProductVO> getProducts() {
        return subscriptionMapper.selectAllProductsForAdmin();
    }

    @Transactional
    public ProductVO updateProduct(int productId, AdminProductRequest request) {
        if (request == null || request.getPrice() < 0 || request.getDurationDays() <= 0) {
            throw new IllegalArgumentException("가격은 0 이상, 이용 기간은 1일 이상이어야 합니다.");
        }

        int updated = subscriptionMapper.updateProductForAdmin(
                productId,
                request.getPrice(),
                request.getDurationDays(),
                request.getDescription(),
                request.isActive());
        if (updated == 0) {
            throw new IllegalArgumentException("상품을 찾을 수 없거나 입력값이 올바르지 않습니다.");
        }
        return subscriptionMapper.selectProductById(productId);
    }
}