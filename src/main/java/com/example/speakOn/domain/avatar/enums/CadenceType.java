package com.example.speakOn.domain.avatar.enums;

public enum CadenceType {

    SHORT(1.15), //빠르게
    MEDIUM(1.0), //보통
    LONG(0.85);  //느리게

    private final double speedRate;

    CadenceType(double speedRate) {
        this.speedRate = speedRate;
    }

    public double getSpeedRate() {
        return speedRate;
    }
}

