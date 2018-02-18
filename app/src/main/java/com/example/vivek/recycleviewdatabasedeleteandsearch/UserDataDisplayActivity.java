package com.example.vivek.recycleviewdatabasedeleteandsearch;

import android.annotation.SuppressLint;
import android.app.SearchManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vivek.recycleviewdatabasedeleteandsearch.Utils.AlertDialogHelper;
import com.example.vivek.recycleviewdatabasedeleteandsearch.Utils.ItemDividerDecoration;
import com.example.vivek.recycleviewdatabasedeleteandsearch.Utils.RecyclerItemClickListener;

import java.util.ArrayList;
import java.util.List;

public class UserDataDisplayActivity extends AppCompatActivity implements AlertDialogHelper.AlertDialogListener, SearchView.OnQueryTextListener {


    // variable of multiselect

    ActionMode mActionMode;
    Menu context_menu;
    ArrayList<ListDataModel> user_list = new ArrayList<>();
    ArrayList<ListDataModel> multiselect_list = new ArrayList<>();
    AlertDialogHelper alertDialogHelper;
    boolean isMultiSelect = false;
    boolean mainrecyclerview = false;
    public boolean arm;


    // Item search

    String searchString = "";

  // temp Array list for storing the actual list items
    public ArrayList<ListDataModel> temp_list = new ArrayList<>();
    public ArrayList<ListDataModel> temp_list1 = new ArrayList<>();


    private AppCompatActivity activity = UserDataDisplayActivity.this;
    private RecyclerView recyclerViewUsers;
    private UserInputRecyclerViewAdapter usersRecyclerAdapter;
    private DatabaseHelper userDatabaseHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_data_display);

        initiateView();
        initiateObjects();
    }

    private void initiateView() {
        recyclerViewUsers = (RecyclerView) findViewById(R.id.recyclerViewUsers);
    }

    private void initiateObjects() {

        alertDialogHelper = new AlertDialogHelper(this);


        usersRecyclerAdapter = new UserInputRecyclerViewAdapter(user_list, multiselect_list, this);
        temp_list = user_list;

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerViewUsers.setLayoutManager(mLayoutManager);
        recyclerViewUsers.setItemAnimator(new DefaultItemAnimator());
        recyclerViewUsers.setHasFixedSize(true);
        recyclerViewUsers.setAdapter(usersRecyclerAdapter);
        recyclerViewUsers.setItemAnimator(new DefaultItemAnimator());
        recyclerViewUsers.addItemDecoration(new ItemDividerDecoration(this, LinearLayoutManager.VERTICAL));
        userDatabaseHelper = new DatabaseHelper(activity);


        recyclerViewUsers.addOnItemTouchListener(new RecyclerItemClickListener(this, recyclerViewUsers, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {


                if (isMultiSelect)
                    multi_select(position);
                else
                    Toast.makeText(getApplicationContext(), user_list.get(position).getFirstName(), Toast.LENGTH_SHORT).show();
            }


            @Override
            public void onItemLongClick(View view, int position) {
                if (!isMultiSelect) {
                    multiselect_list = new ArrayList<ListDataModel>();
                    isMultiSelect = true;

                    if (mActionMode == null) {
                        mActionMode = startActionMode(mActionModeCallback);
                    }
                }


                if (mainrecyclerview) {
                    user_list = temp_list1;
                    multi_select(position);

                } else {
                    user_list = temp_list;
                    multi_select(position);

                }


            }
        }));


        getDataFromSQLite();


    }


    @SuppressLint("StaticFieldLeak")
    private void getDataFromSQLite() {
        // AsyncTask is used that SQLite operation not blocks the UI Thread.
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                user_list.clear();
                user_list.addAll(userDatabaseHelper.getAllUser());

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                usersRecyclerAdapter.notifyDataSetChanged();
            }
        }.execute();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_common_activity, menu);

        MenuItem searchitem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchitem);
        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        TextView searchText = (TextView) searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);

        searchText.setTextColor(Color.parseColor("#FFFFFF"));
        searchText.setHintTextColor(Color.parseColor("#FFFFFF"));
        searchText.setHint("Search Android Version...");
        searchView.setOnQueryTextListener(this);


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();


        switch (id) {
            case android.R.id.home:
                onBackPressed();
                return true;

        }

        return super.onOptionsItemSelected(item);
    }


    public void multi_select(int position) {
        if (mActionMode != null) {
            if (multiselect_list.contains(user_list.get(position)))
                multiselect_list.remove(user_list.get(position));
            else
                multiselect_list.add(user_list.get(position));

            if (multiselect_list.size() > 0)
                mActionMode.setTitle("" + multiselect_list.size());
            else
                mActionMode.setTitle("");

            refreshAdapter();

        }
    }


    public void refreshAdapter() {
        usersRecyclerAdapter.selected_usersList = multiselect_list;
        usersRecyclerAdapter.usersList = user_list;
        usersRecyclerAdapter.notifyDataSetChanged();
    }

    private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            // Inflate a menu resource providing context menu items
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.menu_multi_select, menu);
            context_menu = menu;
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false; // Return false if nothing is done
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.action_delete:
                    alertDialogHelper.showAlertDialog("", "Delete Contact", "DELETE", "CANCEL", 1, false);
                    return true;
                default:
                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mActionMode = null;
            isMultiSelect = false;
            multiselect_list = new ArrayList<ListDataModel>();

            // assing initial list to adapter and refresh
            user_list = temp_list;
            usersRecyclerAdapter.notifyDataSetChanged();

        }
    };

    // AlertDialog Callback Functions

    @Override
    public void onPositiveClick(int from) {
        if (from == 1) {
            if (multiselect_list.size() > 0) {
                for (int i = 0; i < multiselect_list.size(); i++) {


                    try {

                        arm = userDatabaseHelper.deleteFact(multiselect_list.get(i));

                    } catch (Exception e) {
                        Log.e("error", e.toString());
                    }

                    user_list.remove(multiselect_list.get(i));

                    usersRecyclerAdapter.notifyDataSetChanged();


                }


                if (mActionMode != null) {
                    mActionMode.finish();
                }
            }
        }


        if (arm) {

            getDataFromSQLite();
            usersRecyclerAdapter.notifyDataSetChanged();


        } else if (from == 2) {
            if (mActionMode != null) {
                mActionMode.finish();
            }

            ListDataModel mSample = new ListDataModel();
            user_list.add(mSample);
            usersRecyclerAdapter.notifyDataSetChanged();

        }
    }

    @Override
    public void onNegativeClick(int from) {

    }

    @Override
    public void onNeutralClick(int from) {

    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {




        final List<ListDataModel> filteredModelList = filter(user_list, newText);
        if (filteredModelList.size() > 0) {
            usersRecyclerAdapter.setFilter(filteredModelList);
            return true;
        } else {
            Toast.makeText(UserDataDisplayActivity.this, "Not Found", Toast.LENGTH_SHORT).show();
            return false;
        }
    }


    // Search Method
    private List<ListDataModel> filter(List<ListDataModel> models, String query) {

        query = query.toLowerCase();
        this.searchString = query;

        final ArrayList<ListDataModel> filteredModelList = new ArrayList<>();
        for (ListDataModel model : models) {
            final String text = model.getFirstName().toLowerCase();
            if (text.contains(query)) {
                filteredModelList.add(model);
            }
        }
        usersRecyclerAdapter = new UserInputRecyclerViewAdapter(filteredModelList, multiselect_list, UserDataDisplayActivity.this);
        mainrecyclerview = true;
        temp_list1 = filteredModelList;
        recyclerViewUsers.setLayoutManager(new LinearLayoutManager(UserDataDisplayActivity.this));
        recyclerViewUsers.setAdapter(usersRecyclerAdapter);
        usersRecyclerAdapter.notifyDataSetChanged();

        return filteredModelList;
    }


}