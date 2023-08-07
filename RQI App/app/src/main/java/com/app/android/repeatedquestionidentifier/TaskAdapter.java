package com.app.android.repeatedquestionidentifier;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.app.android.repeatedquestionidentifier.database.TaskEntry;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {
    // Constant for date format
    private static final String DATE_FORMAT = "dd/MM/yyy : HH:mm";
    final private ItemClickListener mItemClickListener;// Class variables for the List that holds task data and the Context
    private List<TaskEntry> mTaskEntries;
    private Context mContext;
    // Date formatter
    private SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());

    public TaskAdapter(Context context, ItemClickListener listener) {
        mContext = context;
        mItemClickListener = listener;
    }

    @Override
    public TaskViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.task_layout, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TaskViewHolder holder, int position) {
        // Determine the values of the wanted data
        TaskEntry taskEntry = mTaskEntries.get(position);
        String q1 = taskEntry.getQuestion1();
        String q2 = taskEntry.getQuestion2();
        String checkedAt = dateFormat.format(taskEntry.getCheckedAt());
        Integer indicator = taskEntry.getIndicator();

        //Set values
        holder.ques1.setText(q1);
        holder.ques2.setText(q2);
        holder.checkedAtView.setText(checkedAt);
        if(indicator==1){
            holder.masterLayout.setBackgroundResource(R.drawable.same_bg);
        } else if(indicator==0){
            holder.masterLayout.setBackgroundResource(R.drawable.different_bg);
        }
    }

    //Returns the number of items to display
    @Override
    public int getItemCount() {
        if (mTaskEntries == null) {
            return 0;
        }
        return mTaskEntries.size();
    }

     //When data changes, this method updates the list of taskEntrie and notifies the adapter to use the new values on it
    public List<TaskEntry> getTasks(){
        return mTaskEntries;
    }

    public void setTasks(List<TaskEntry> taskEntries) {
        mTaskEntries = taskEntries;
        notifyDataSetChanged();
    }

    public interface ItemClickListener {
        void onItemClickListener(int itemId);
    }

    // Inner class for creating ViewHolders
    class TaskViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // Class variables for the task description and priority TextViews
        TextView ques1;
        TextView ques2;
        TextView checkedAtView;
        LinearLayout masterLayout;
        public TaskViewHolder(View itemView) {
            super(itemView);
            ques1 = itemView.findViewById(R.id.ques1);
            ques2 = itemView.findViewById(R.id.ques2);
            checkedAtView = itemView.findViewById(R.id.taskCheckedAt);
            itemView.setOnClickListener(this);
            masterLayout=itemView.findViewById(R.id.master_linear_layout);
        }

        @Override
        public void onClick(View view) {
            int elementId = mTaskEntries.get(getAdapterPosition()).getId();
            mItemClickListener.onItemClickListener(elementId);
        }
    }
}