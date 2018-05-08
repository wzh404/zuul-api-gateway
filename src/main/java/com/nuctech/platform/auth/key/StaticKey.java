package com.nuctech.platform.auth.key;

/**
 * Created by @author wangzunhui on 2018/4/15.
 */
public class StaticKey implements Key{
    private static final String[] KEYS = {
            "0c798770-9b3a-4c2c-91d4-76034acd460a",
            "e5c79afd-76f7-4ce9-a507-12e3829fcee3",
            "61ad65e9-2ec4-4533-bbc1-aec1df434c1d",
            "89c230d1-7f12-426c-b6ed-251d685be839",
            "64a74228-7246-42fe-9a96-d6fadde5b582",
            "8ad7d809-e2dd-4784-a22d-2092eb87aaeb",
            "a0af301f-6beb-4803-805c-94ac63ac5ee9",
            "61dfd4a3-e01b-4fb0-bf25-2d7ad1514e1a",
            "3ae47e43-84c6-42fd-a2aa-cf9aea103e21",
            "3e5528de-1948-4798-b670-e9839d6f7492",
            "1d9b27dd-9326-41d0-a003-4362efcdeab0",
            "7b06a659-3bae-4acf-a455-c4abfab6ae95",
            "286cde55-9532-4cbb-a5a7-26d91fcf4bae",
            "8bf2ab7b-f5ad-4da9-8d91-988a6f66a785",
            "814fd8d2-b7a1-463e-ad2b-f67fcde37771",
            "bddd717b-0824-4804-8656-e537a294bbd5",
            "c681f9b3-7b34-4b0a-a076-4bbe44ee280c",
            "97dc0b4d-b431-4b8b-816d-e0277b5b13ec",
            "e461a90e-39a4-4b08-9d5d-e316f7e8e6ec",
            "45c1164c-5cd4-44d1-8076-70c897bb1515",
            "128301b3-bdfa-465e-b1dc-4fa53f8280c7",
            "3a8a00e4-596d-4c08-af99-beb804400bcb",
            "a97f30b6-0145-4421-b1ab-d24567db02a8",
            "f40ac139-95dd-4663-90a6-56211be66db5",
            "83f95634-1cfd-46df-ba44-f3596b2a35a8",
            "95eb8a10-3ea3-4857-b714-6fedaf6a1f75",
            "f6df861a-afb8-4670-b3dd-48ea8984b153",
            "c32abc9b-7338-4973-838e-181d1e5eb80a",
            "290677ca-cc2f-473a-b247-9c7e7fe1944f",
            "93ba0248-4a6d-4763-9a8f-9d92f0934753",
            "301571bb-c5f4-40b7-8afc-9f8b208aba65",
            "50949adb-9fb1-40ca-812d-b85d8d765c9f",
    };

    @Override
    public String get(int index) {
        return KEYS[Math.abs(index) % KEYS.length];
    }
}
