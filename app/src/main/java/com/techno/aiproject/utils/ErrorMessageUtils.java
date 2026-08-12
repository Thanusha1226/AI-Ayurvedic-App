package com.techno.aiproject.utils;

/**
 * Utility class for generating user-friendly error and status messages
 * with multi-language support
 */
public class ErrorMessageUtils {

    public static String getGeminiLimitReachedMessage(long remainingMs, String language) {
        long hours = remainingMs / (1000 * 60 * 60);
        long minutes = (remainingMs % (1000 * 60 * 60)) / (1000 * 60);

        if (Constants.LANG_SINHALA.equals(language)) {
            if (hours > 0) {
                return String.format("⚠️ API ගැණුම් සීමාව ළඟා විය! කරුණාකර පැය %d සහ මිනිත්තු %d බලා සිටින්න.", hours, minutes);
            } else {
                return String.format("⚠️ API ගැණුම් සීමාව ළඟා විය! කරුණාකර මිනිත්තු %d බලා සිටින්න.", minutes);
            }
        } else if (Constants.LANG_TAMIL.equals(language)) {
            if (hours > 0) {
                return String.format("⚠️ API கோரிக்கை வரம்பை அடைந்துவிட்டீர்கள்! %d மணிநேரம் %d நிமிடங்கள் பொறுக்கவும்.", hours, minutes);
            } else {
                return String.format("⚠️ API கோரிக்கை வரம்பை அடைந்துவிட்டீர்கள்! %d நிமிடங்கள் பொறுக்கவும்.", minutes);
            }
        } else {
            // English (default)
            if (hours > 0) {
                return String.format("⚠️ API request limit reached! Please wait %d hours and %d minutes.", hours, minutes);
            } else {
                return String.format("⚠️ API request limit reached! Please wait %d minutes.", minutes);
            }
        }
    }

    public static String getScanLimitReachedMessage(long remainingMs, String language) {
        long hours = remainingMs / (1000 * 60 * 60);
        long minutes = (remainingMs % (1000 * 60 * 60)) / (1000 * 60);

        if (Constants.LANG_SINHALA.equals(language)) {
            if (hours > 0) {
                return String.format("⚠️ ස්කෑන් සීමාව ළඟා විය! කරුණාකර පැය %d සහ මිනිත්තු %d බලා සිටින්න.", hours, minutes);
            } else {
                return String.format("⚠️ ස්කෑන් සීමාව ළඟා විය! කරුණාකර මිනිත්තු %d බලා සිටින්න.", minutes);
            }
        } else if (Constants.LANG_TAMIL.equals(language)) {
            if (hours > 0) {
                return String.format("⚠️ ஸ்கேன் வரம்பை அடைந்துவிட்டீர்கள்! %d மணிநேரம் %d நிமிடங்கள் பொறுக்கவும்.", hours, minutes);
            } else {
                return String.format("⚠️ ஸ்கேன் வரம்பை அடைந்துவிட்டீர்கள்! %d நிமிடங்கள் பொறுக்கவும்.", minutes);
            }
        } else {
            // English (default)
            if (hours > 0) {
                return String.format("⚠️ Scan limit reached! Please wait %d hours and %d minutes.", hours, minutes);
            } else {
                return String.format("⚠️ Scan limit reached! Please wait %d minutes.", minutes);
            }
        }
    }

    public static String getConnectionErrorMessage(String language) {
        if (Constants.LANG_SINHALA.equals(language)) {
            return "🔌 අන්තර්ජාලයට සংযුක්ත වන්න කරුණාකර.";
        } else if (Constants.LANG_TAMIL.equals(language)) {
            return "🔌 தயவுசெய்து இணையத்துடன் இணைந்து கொள்ளவும்.";
        } else {
            return "🔌 Please connect to the internet and try again.";
        }
    }

    public static String getAnalysisCompleteMessage(int remainingRequests, String language) {
        if (Constants.LANG_SINHALA.equals(language)) {
            if (remainingRequests > 0) {
                return String.format("✓ විශ්ලේෂණය සම්පූර්ණ! අද %d API ගැණුම්(ල) ඉතිරිව ඇත.", remainingRequests);
            } else {
                return "✓ විශ්ලේෂණය සම්පූර්ණ! ඔබ අද ගැණුම් සීමාව ළඟා විය.";
            }
        } else if (Constants.LANG_TAMIL.equals(language)) {
            if (remainingRequests > 0) {
                return String.format("✓ பகுப்பாய்வு முடிந்தது! இன்று %d API கோரிக்கை(கள்) மீதம் உள்ளது.", remainingRequests);
            } else {
                return "✓ பகுப்பாய்வு முடிந்தது! இன்று உங்கள் வரம்பை அடைந்துவிட்டீர்கள்.";
            }
        } else {
            if (remainingRequests > 0) {
                return String.format("✓ Analysis complete! %d API request(s) remaining today.", remainingRequests);
            } else {
                return "✓ Analysis complete! You've reached your daily limit.";
            }
        }
    }

    public static String getWarningLowRequests(String language) {
        if (Constants.LANG_SINHALA.equals(language)) {
            return "⚠️ අවවාදයි: අද ඉතිරි ගැණුම් ඉතා අඩුයි!";
        } else if (Constants.LANG_TAMIL.equals(language)) {
            return "⚠️ எச்சரிக்கை: இன்று மீதமுள்ள கோரிக்கைகள் மிகவும் குறைவாக உள்ளது!";
        } else {
            return "⚠️ Warning: Very few API requests remaining today!";
        }
    }

    public static String getImageValidationError(String language) {
        if (Constants.LANG_SINHALA.equals(language)) {
            return "📷 පින්තූර සත්යාපනය අසාර්థක: අපූපගත පිටපත නොමැත.";
        } else if (Constants.LANG_TAMIL.equals(language)) {
            return "📷 பட சரிபார்ப்பு தோல்வி: பதிவேற்றப்பட்ட படத்தில் செடி இல்லை.";
        } else {
            return "📷 Image validation failed: No plant detected in the uploaded image.";
        }
    }
}
