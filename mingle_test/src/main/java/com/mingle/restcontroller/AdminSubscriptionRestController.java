package com.mingle.restcontroller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mingle.dto.AdminProductRequest;
import com.mingle.service.AdminSubscriptionService;
import com.mingle.vo.ProductVO;

@RestController
@RequestMapping("/api/admin/subscriptions")
public class AdminSubscriptionRestController {

    @Autowired
    private AdminSubscriptionService adminSubscriptionService;

    @GetMapping("/products")
    public ResponseEntity<List<ProductVO>> getProducts(HttpServletRequest request) {
        if (!isAdmin(request)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(adminSubscriptionService.getProducts());
    }

    @PutMapping("/products/{productId}")
    public ResponseEntity<ProductVO> updateProduct(
            @PathVariable int productId,
            @RequestBody AdminProductRequest product,
            HttpServletRequest request) {
        if (!isAdmin(request)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(adminSubscriptionService.updateProduct(productId, product));
    }

    private boolean isAdmin(HttpServletRequest request) {
        return request.isUserInRole("ADMIN");
    }
}
