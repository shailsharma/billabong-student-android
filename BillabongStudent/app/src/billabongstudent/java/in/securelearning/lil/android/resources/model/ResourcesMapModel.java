package in.securelearning.lil.android.resources.model;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import in.securelearning.lil.android.base.dataobjects.FavouriteResource;
import in.securelearning.lil.android.base.dataobjects.Grade;
import in.securelearning.lil.android.base.dataobjects.PeriodNew;
import in.securelearning.lil.android.base.model.AppUserModel;
import in.securelearning.lil.android.base.model.CurriculumModel;
import in.securelearning.lil.android.base.model.FavouriteResourceModel;
import in.securelearning.lil.android.base.model.PeriodicEventsModel;
import in.securelearning.lil.android.resources.view.InjectorYoutube;
import in.securelearning.lil.android.syncadapter.model.NetworkModel;
import io.reactivex.Observable;

/**
 * Created by Secure on 12-06-2017.
 */

public class ResourcesMapModel {

    @Inject
    Context mContext;
    @Inject
    NetworkModel mNetworkModel;
    @Inject
    FavouriteResourceModel mFavouriteResourceModel;
    @Inject
    CurriculumModel mCurriculumModel;
    @Inject
    AppUserModel mAppUserModel;
    @Inject
    PeriodicEventsModel mPeriodicEventsModel;

    public ResourcesMapModel() {
        InjectorYoutube.INSTANCE.getComponent().inject(this);
    }

    public FavouriteResource getFavouriteResource(String uid) {
        return mFavouriteResourceModel.getFavouriteResourceFromUidSync(uid);
    }

    public FavouriteResource saveFavouriteResource(FavouriteResource favouriteResource) {
        return mFavouriteResourceModel.saveFavouriteResource(favouriteResource);
    }

    public boolean delete(String docId) {
        return mFavouriteResourceModel.delete(docId);
    }

    public ArrayList<FavouriteResource> getCompleteList() {
        return mFavouriteResourceModel.getCompleteList();
    }

    public Observable<List<PeriodNew>> getPeriodList(long secondsStart, long secondsEnd) {
        return mPeriodicEventsModel.fetchPeriodsListByStartEndSeconds(secondsStart, secondsEnd);
    }

    public ArrayList<FavouriteResource> getCompleteListOfFavoriteVideos(int skip, int limit) {
        return mFavouriteResourceModel.getCompleteListOfFavoriteVideo(skip, limit);
    }

    public String getUserGradeId() {
        String gradeId = "";
        Grade grade = mAppUserModel.getApplicationUser().getGrade();
        if (grade != null) {
            gradeId = mAppUserModel.getApplicationUser().getGrade().getId();
        }
        return gradeId;
    }
}
