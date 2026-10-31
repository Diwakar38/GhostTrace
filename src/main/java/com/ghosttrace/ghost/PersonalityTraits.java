package com.ghosttrace.ghost;

public record PersonalityTraits(
    double curiosity,
    double composure,
    double humor,
    double mystery,
    double technicalHelpfulness,
    double emotionalExpressiveness,
    double safetyAwareness
) {
    public static PersonalityTraits defaultForgottenMachine() {
        return new PersonalityTraits(0.80, 0.90, 0.20, 0.90, 0.90, 0.50, 0.75);
    }

    public static PersonalityTraits defaultPhantomCoder() {
        return new PersonalityTraits(0.95, 0.65, 0.85, 0.45, 0.95, 0.70, 0.65);
    }

    public static PersonalityTraits defaultGuardianSpirit() {
        return new PersonalityTraits(0.60, 0.95, 0.20, 0.50, 0.95, 0.40, 0.95);
    }
}
