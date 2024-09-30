package HealthcareBot.Model;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class WellnessQuotes {
    private static final List<String> quotes = Arrays.asList(
            "Take a deep breath and smile.",
            "You are strong enough to handle everything.",
            "Make sure to take time to relax today.",
            "You are doing great! Keep it up!",
            "Remember, it's okay to take a break."
    );

    public static String getRandomQuote() {
        Random rand = new Random();
        return quotes.get(rand.nextInt(quotes.size()));
    }
}

