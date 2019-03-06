package in.securelearning.lil.android.provider;

import android.content.SearchRecentSuggestionsProvider;

import in.securelearning.lil.android.app.BuildConfig;

public class SearchSuggestionProvider extends SearchRecentSuggestionsProvider {

    public static final String AUTHORITY = BuildConfig.APPLICATION_ID + ".provider.SearchSuggestionProvider";
    public static final int MODE = DATABASE_MODE_QUERIES;

    public SearchSuggestionProvider() {
        setupSuggestions(AUTHORITY, MODE);
    }


}
