package com.eric6166.inventory.utils;

public final class Constants {
    public static final String SORT_COLUMN_ID = "id";
    public static final String SORT_COLUMN_NAME = "name";
    public static final String SORT_COLUMN_DESCRIPTION = "description";
    public static final String SORT_COLUMN_PRICE = "price";
    public static final String SORT_COLUMN_CATEGORY_ID = "categoryId";

    public static final String CACHE_NAME_PRODUCT_FIND_BY_ID = "product.findById";

    private Constants() {
        throw new IllegalStateException("Utility class");
    }
}
