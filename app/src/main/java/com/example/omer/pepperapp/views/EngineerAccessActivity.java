package com.example.omer.pepperapp.views;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.omer.pepperapp.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by Omer on 9/15/2017.
 */

public class EngineerAccessActivity extends ActionBarActivity {

    private static final int REQUEST_CODE = 123;

    private static final String VOCABS_KEY = "vocabs";

    public static void start(Activity activity,ArrayList<String> vocabs){
        Intent intent = new Intent(activity,EngineerAccessActivity.class);
        intent.putStringArrayListExtra(VOCABS_KEY,vocabs);
        activity.startActivityForResult(intent,REQUEST_CODE);
    }

    public static ArrayList<String> extractVocabs(Intent intent){
        if(intent==null){
            return new ArrayList<>();
        }
        return intent.getStringArrayListExtra(VOCABS_KEY);
    }

    private EditText newGreetingEditText;
    private Button addGreetingButton;
    private Button saveButton;
    private RecyclerView currentVocabRecyclerView;
    private CurrentVocabAdapter currentVocabAdapter;

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.eng_access_activity);

        newGreetingEditText = (EditText)findViewById(R.id.et_new_greeting);
        addGreetingButton   = (Button)findViewById(R.id.btn_add);
        saveButton          = (Button)findViewById(R.id.btn_save);
        currentVocabRecyclerView = (RecyclerView)findViewById(R.id.rv_cur_greetings);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        currentVocabRecyclerView.setLayoutManager(layoutManager);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this,
                layoutManager.getOrientation());

        currentVocabRecyclerView.addItemDecoration(dividerItemDecoration);

        ArrayList<String> vocab = getIntent().getStringArrayListExtra(VOCABS_KEY);

        currentVocabAdapter  = new CurrentVocabAdapter(vocab);
        currentVocabRecyclerView.setAdapter(currentVocabAdapter);

        addGreetingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newGreeting = newGreetingEditText.getText().toString();
                if(!newGreeting.isEmpty()){
                    currentVocabAdapter.addVocab(newGreeting);
                }
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveAndReturnResults();
            }
        });


    }




    public void saveAndReturnResults() {
        Log.d("PEPPER LOG","Finishing Eng Access");
        Intent intent = new Intent();
        intent.putStringArrayListExtra(VOCABS_KEY,currentVocabAdapter.getCurrentVocabulary());
        setResult(RESULT_OK,intent);
        finish();
    }

    private class CurrentVocabAdapter extends RecyclerView.Adapter<VocabsViewHolder>{

        private ArrayList<String> currentVocabulary;

        public CurrentVocabAdapter(ArrayList<String> vocab){
            currentVocabulary = new ArrayList<>(vocab);
        }

        public void addVocab(String vocab){
            currentVocabulary.add(vocab);
            notifyDataSetChanged();
        }

        @Override
        public VocabsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_vocab,null);
            return new VocabsViewHolder(view);
        }

        @Override
        public void onBindViewHolder(VocabsViewHolder holder, final int position) {
            holder.vocabTextView.setText(currentVocabulary.get(position));
            holder.deleteTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    currentVocabulary.remove(position);
                    notifyDataSetChanged();
                }
            });
        }

        @Override
        public int getItemCount() {
            return currentVocabulary.size();
        }

        public ArrayList<String> getCurrentVocabulary() {
            return currentVocabulary;
        }
    }

    private class VocabsViewHolder extends RecyclerView.ViewHolder{

        TextView vocabTextView;
        Button deleteTextView;

        public VocabsViewHolder(View itemView) {
            super(itemView);
            vocabTextView = (TextView) itemView.findViewById(R.id.tv_vocab);
            deleteTextView = (Button) itemView.findViewById(R.id.btn_delete);
        }
    }
}
