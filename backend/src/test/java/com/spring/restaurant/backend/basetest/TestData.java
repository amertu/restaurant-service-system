package com.spring.restaurant.backend.basetest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public interface TestData {

    Long ID = 1L;
    String TEST_NEWS_TITLE = "Title";
    String TEST_NEWS_SUMMARY = "Summary";
    String TEST_NEWS_TEXT = "TestMessageText";
    LocalDateTime TEST_NEWS_PUBLISHED_AT =
        LocalDateTime.of(2019, 11, 13, 12, 15, 0, 0);

    String BASE_URI = "/api/v1";
    String MESSAGE_BASE_URI = BASE_URI + "/messages";
    String RESERVATION_BASE_URI = BASE_URI + "/reservations";
    String TABLE_BASE_URI = BASE_URI + "/tables";
    String DISH_BASE_URI = BASE_URI + "/dishes";
    String AUTHENTICATION_BASE_URI = BASE_URI + "/authentication";
    String FILTER_RESERVATIONS_BASE_URI = RESERVATION_BASE_URI + "/filter";
    String LOGIN_BASE_URI = BASE_URI + "/login";
    String USER_BASE_URI = BASE_URI + "/users";
    String FLOOR_LAYOUT_BASE_URI = BASE_URI + "/layout";
    String REGISTER_BASE_URI = USER_BASE_URI;

    String ADMIN_USER = "admin@email.com";
    List<String> ADMIN_ROLES = new ArrayList<>() {
        {
            add("ROLE_ADMIN");
            add("ROLE_USER");
        }
    };
    String DEFAULT_USER = "admin@email.com";
    List<String> USER_ROLES = new ArrayList<>() {
        {
            add("ROLE_USER");
        }
    };

    String TEST_USER_FIRSTNAME = "Walid";
    String TEST_USER_LASTNAME = "Said";
    String TEST_USER_EMAIL = "walid@yahoo.com";
    boolean TEST_USER_ADMIN = false;
    Long TEST_USER_SSNR = 1111220390L;
    String TEST_USER_PASSWORD = "password";

    String TEST_USER_INVALID_EMAIL = "notfound@email.com";

    boolean TEST_USER_BLOCKED = false;

    String TEST_USER_FIRSTNAME2 = "Samir";
    String TEST_USER_LASTNAME2 = "Barqaui";
    Long TEST_USER_SSNR2 = 1111220391l;

    LocalDateTime TEST_START_DATE_TIME_FOR_VALID_RESERVATION =
        LocalDateTime.of(2099, 5, 1, 12, 0, 0);

    LocalDateTime TEST_END_DATE_TIME_FOR_VALID_RESERVATION =
        LocalDateTime.of(2099, 5, 1, 14, 0, 0);

    String TEST_GUEST_FOR_VALID_RESERVATION = "Gruppe 19";
    Integer TEST_GUEST_COUNT_FOR_VALID_RESERVATION = 6;

    String TEST_CONTACT_INFORMATION_FOR_VALID_RESERVATOIN = "+43 6...";
    String TEST_COMMENT_FOR_VALID_RESERVATION = "No alcohol for these guests!";

    Integer TEST_TABLE_CAPACITY_FOR_VALID_RESERVATION = 6;

    Long TEST_TABLE_TABLE_NUM = 99L;
    String TEST_TABLE_POS_DESCRIPTION = "somewhere over the rainbow";
    Boolean TEST_TABLE_ACTIVE = false;
    Integer TEST_TABLE_SEAT_COUNT = 4;

    String TEST_DISH_NAME = "Pizza";
    Long TEST_DISH_PRICE = 850L;

    String TEST_DISH_NAME_EDIT = "Lasagne";
    Long TEST_DISH_PRICE_EDIT = 675L;

    String TEST_LAYOUT_SERIALIZATION = "This is not a canvas but a Teststring";
    String TEST_LAYOUT_SERIALIZATION_UPDATE = "THIS IS ANOTHER TEST!";
}
