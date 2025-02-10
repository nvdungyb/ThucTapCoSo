package com.shopme.service;

import com.shopme.Reposistory.WishlistReposistory;
import com.shopme.common.shop.WishlistItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WishlistService {
    private final WishlistReposistory wishlistReposistory;
    private Logger logger = LoggerFactory.getLogger(WishlistService.class);

    public WishlistService(WishlistReposistory wishlistReposistory) {
        this.wishlistReposistory = wishlistReposistory;
    }

    // todo: need to support pagination
    public List<WishlistItem> getWishlist(long userId) {
        return wishlistReposistory.findAllByUser_Id(userId);
    }
}
