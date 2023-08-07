package com.app.android.repeatedquestionidentifier;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import com.app.android.repeatedquestionidentifier.database.AppDatabase;
import com.app.android.repeatedquestionidentifier.database.TaskEntry;

public class AddTaskViewModel extends ViewModel {
    private LiveData<TaskEntry> task;
    public AddTaskViewModel(AppDatabase database, int taskId){
        task=database.taskDao().loadTaskById(taskId);
    }
    public LiveData<TaskEntry> getTask(){
        return task;
    }
}
