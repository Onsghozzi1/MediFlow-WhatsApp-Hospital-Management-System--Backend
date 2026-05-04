package com.example.MediFlow.services;

public interface IAvatarGeneratorService {
    public byte[] generateAvatar(String firstName, String lastName) throws Exception;

}
