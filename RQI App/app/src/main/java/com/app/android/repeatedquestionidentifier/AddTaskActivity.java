 package com.app.android.repeatedquestionidentifier;
 import android.content.Intent;
 import android.graphics.drawable.Drawable;
 import android.os.Build;
 import android.os.Bundle;
 import android.view.View;
 import android.view.Window;
 import android.widget.Button;
 import android.widget.EditText;
 import android.widget.TextView;
 import android.widget.Toast;
 import androidx.appcompat.app.AppCompatActivity;
 import androidx.appcompat.widget.Toolbar;
 import androidx.core.content.ContextCompat;
 import androidx.lifecycle.Observer;
 import androidx.lifecycle.ViewModelProvider;
 import com.android.volley.Request;
 import com.android.volley.RequestQueue;
 import com.android.volley.VolleyError;
 import com.android.volley.toolbox.StringRequest;
 import com.android.volley.toolbox.Volley;
 import com.app.android.repeatedquestionidentifier.database.AppDatabase;
 import com.app.android.repeatedquestionidentifier.database.TaskEntry;
 import org.json.JSONException;
 import org.json.JSONObject;
 import java.util.Date;
 import java.util.HashMap;
 import java.util.Map;

 public class AddTaskActivity extends AppCompatActivity {
    public static final String EXTRA_TASK_ID = "extraTaskId";
    public static final String INSTANCE_TASK_ID = "instanceTaskId";

    // Constant for default task id to be used when not in update mode
    private static final int DEFAULT_TASK_ID = -1;

    EditText question1,question2;
    Button checkButton;
    private int mTaskId = DEFAULT_TASK_ID;
    private AppDatabase mDb;
    TextView currDATE;
    TextView result;
    Date prevDate;
    String url="http://ec2-54-168-58-245.ap-northeast-1.compute.amazonaws.com:5000/predict";
    private int indicator;

     protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);
        initViews();

        //setting status bar
        Window window = AddTaskActivity.this.getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(ContextCompat.getColor(AddTaskActivity.this,R.color.theme_color));
        }

        Toolbar toolbar=findViewById(R.id.addtaskToolbar);
        toolbar.setTitle("Check Similarity");
        Drawable drawable = ContextCompat.getDrawable(AddTaskActivity.this, R.drawable.back_icon);
        toolbar.setNavigationIcon(drawable);
        toolbar.setTitleTextColor(ContextCompat.getColor(getApplicationContext(),R.color.black));
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                back_button_pressed();
            }
        });


        mDb = AppDatabase.getInstance(getApplicationContext());
        if (savedInstanceState != null && savedInstanceState.containsKey(INSTANCE_TASK_ID)) {
            mTaskId = savedInstanceState.getInt(INSTANCE_TASK_ID, DEFAULT_TASK_ID);
        }

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(EXTRA_TASK_ID)) {
            if (mTaskId == DEFAULT_TASK_ID) {
                // populate the UI
                mTaskId = intent.getIntExtra(EXTRA_TASK_ID, DEFAULT_TASK_ID);
                AddTaskViewModelFactory factory = new AddTaskViewModelFactory(mDb, mTaskId);
                AddTaskViewModel viewModel = new ViewModelProvider(this,factory).get(AddTaskViewModel.class);
                viewModel.getTask().observe(this, new Observer<TaskEntry>() {
                    @Override
                    public void onChanged(TaskEntry taskEntry) {
                        viewModel.getTask().removeObserver(this);
                        populateUI(taskEntry);
                    }
                });
            }
        }

        checkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                result.setText("");
                String ques1 = question1.getText().toString();
                String ques2 = question2.getText().toString();
                if (ques1.length() == 0) {
                    Toast.makeText(AddTaskActivity.this, "Question1 can't be empty", Toast.LENGTH_SHORT).show();
                } else if (ques2.length() == 0) {
                    Toast.makeText(AddTaskActivity.this, "Question2 can't be empty", Toast.LENGTH_SHORT).show();
                } else {
                    StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                            new com.android.volley.Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    try {
                                        JSONObject jsonObject = new JSONObject(response);
                                        String data = jsonObject.getString("is_duplicate");
                                        if (data.equals("1")) {
                                            result.setText("Both Question1 and Question2 have SAME MEANING");
                                            result.setTextColor(getResources().getColor(R.color.theme_color));
                                            indicator=1;
                                        } else {
                                            result.setText("Both Question1 and Question2 have DIFFERENT MEANING");
                                            result.setTextColor(getResources().getColor(R.color.red));
                                            indicator=0;
                                        }
                                    } catch (JSONException e) {
                                        Toast.makeText(AddTaskActivity.this, "Error!", Toast.LENGTH_SHORT).show();
                                        e.printStackTrace();
                                    }
                                }
                            },
                            new com.android.volley.Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Toast.makeText(AddTaskActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }) {

                        @Override
                        protected Map<String, String> getParams() {
                            Map<String, String> params = new HashMap<String, String>();
                            params.put("question1", question1.getText().toString());
                            params.put("question2", question2.getText().toString());
                            return params;
                        }
                    };

                    RequestQueue queue = Volley.newRequestQueue(AddTaskActivity.this);
                    queue.add(stringRequest);
                }
            }
        });
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(INSTANCE_TASK_ID, mTaskId);
        super.onSaveInstanceState(outState);
    }

    private void initViews() {
        question1 = findViewById(R.id.question1);
        question2 = findViewById(R.id.question2);
        currDATE=findViewById(R.id.taskCheckedAt);
        checkButton=findViewById(R.id.check);
        result=findViewById(R.id.result);
        indicator=-1;
    }

    private void populateUI(TaskEntry task) {
        if (task == null)
            return;
        prevDate=task.getCheckedAt();
        question1.setText(task.getQuestion1());
        question2.setText(task.getQuestion2());
    }

    public void onSaveButtonClicked() {
        String q1 = question1.getText().toString();
        String q2 = question2.getText().toString();
        int priority = getPriorityFromViews();
        Date date = new Date();

        if(q1.length()!=0 && q2.length()!=0 && indicator>=0){
            final TaskEntry taskEntry1 = new TaskEntry(q1,q2,priority,date,indicator);
            final TaskEntry taskEntry2 = new TaskEntry(q1,q2,priority,prevDate,indicator);
            AppExecutors.getInstance().diskIO().execute(new Runnable() {
                @Override
                public void run() {
                    if (mTaskId == DEFAULT_TASK_ID) {
                        mDb.taskDao().insertTask(taskEntry1);
                    } else {
                        taskEntry2.setId(mTaskId);
                        mDb.taskDao().updateTask(taskEntry2);
                    }
                }
            });
        }
        finish();
    }

    public int getPriorityFromViews() {
        int priority = 1;
        return priority;
    }

    @Override
    public void onBackPressed() {
        back_button_pressed();
    }

    private void back_button_pressed(){
            onSaveButtonClicked();
    }
}