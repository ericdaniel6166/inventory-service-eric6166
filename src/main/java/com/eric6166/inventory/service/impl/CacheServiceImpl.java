package com.eric6166.inventory.service.impl;

import com.eric6166.inventory.service.CacheService;
import com.eric6166.inventory.utils.Constants;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

@Service
public class CacheServiceImpl implements CacheService {

    @CacheEvict(value = {Constants.CACHE_NAME_PRODUCT_FIND_BY_ID}, key = "#id")
    @Override
    public void evictProductFindById(Long id) {
        //
    }
}
