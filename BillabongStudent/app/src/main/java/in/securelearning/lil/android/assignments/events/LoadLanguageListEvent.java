package in.securelearning.lil.android.assignments.events;

import java.util.ArrayList;

import in.securelearning.lil.android.base.dataobjects.Language;

/**
 * Enter Copyright Javadoc Comments here
 * <p>
 * Created by Pushkar Raj 7/28/2016.
 */
public class LoadLanguageListEvent {
    private final ArrayList<Language> mlLanguages;

    public LoadLanguageListEvent(ArrayList<Language> languages) {
        this.mlLanguages = languages;
    }

    public ArrayList<Language> getLanguageList() {
        return mlLanguages;
    }
}
