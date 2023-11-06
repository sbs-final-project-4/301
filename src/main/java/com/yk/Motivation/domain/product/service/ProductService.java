package com.yk.Motivation.domain.product.service;

import com.yk.Motivation.base.rsData.RsData;
import com.yk.Motivation.domain.lecture.entity.Lecture;
import com.yk.Motivation.domain.member.entity.Member;
import com.yk.Motivation.domain.product.entity.Product;
import com.yk.Motivation.domain.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService {
    private final ProductRepository productRepository;

    @Transactional
    public RsData<Lecture> create(Lecture lecture, Member producer, int price, boolean isFree) {
        Product product = Product.builder()
                .lecture(lecture)
                .producer(producer)
                .price(price)
                .isFree(isFree)
                .build();

        productRepository.save(product);

        return new RsData<>("S-1", lecture.getId() + "번 강의가 등록되었습니다.", lecture);
    }
}