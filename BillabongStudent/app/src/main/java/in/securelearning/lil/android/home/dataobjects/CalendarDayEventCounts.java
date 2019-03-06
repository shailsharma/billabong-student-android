package in.securelearning.lil.android.home.dataobjects;

import java.io.Serializable;

import in.securelearning.lil.android.base.dataobjects.CalendarDay;

/**
 * Created by Chaitendra on 12-Jul-17.
 */

public class CalendarDayEventCounts implements Serializable {
    private int activitiesCounts = 0;
    private int personalCounts = 0;
    private int announcementsCounts = 0;
    private int holidayEventCounts  = 0;
    private int examEventCounts  = 0;
    private int vacationEventCounts = 0;
    private int celebrationEventCounts = 0;
    private int trainingSessionCounts = 0;

    public int getCelebrationEventCounts() {
        return celebrationEventCounts;
    }

    public void setCelebrationEventCounts(int celebrationEventCounts) {
        this.celebrationEventCounts = celebrationEventCounts;
    }

    public int getHolidayEventCounts() {
        return holidayEventCounts;
    }

    public void setHolidayEventCounts(int holidayEventCounts) {
        this.holidayEventCounts = holidayEventCounts;
    }

    public int getExamEventCounts() {
        return examEventCounts;
    }

    public void setExamEventCounts(int examEventCounts) {
        this.examEventCounts = examEventCounts;
    }

    public int getVacationEventCounts() {
        return vacationEventCounts;
    }

    public void setVacationEventCounts(int vacationEventCounts) {
        this.vacationEventCounts = vacationEventCounts;
    }

    public int getActivitiesCounts() {
        return activitiesCounts;
    }

    public void setActivitiesCounts(int activitiesCounts) {
        this.activitiesCounts = activitiesCounts;
    }

    public int getPersonalCounts() {
        return personalCounts;
    }

    public void setPersonalCounts(int personalCounts) {
        this.personalCounts = personalCounts;
    }

    public int getAnnouncementsCounts() {
        return announcementsCounts;
    }

    public void setAnnouncementsCounts(int announcementsCounts) {
        this.announcementsCounts = announcementsCounts;
    }

    public int getTrainingSessionCounts() {
        return trainingSessionCounts;
    }

    public void setTrainingSessionCounts(int trainingSessionCounts) {
        this.trainingSessionCounts = trainingSessionCounts;
    }
}
