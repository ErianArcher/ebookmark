package top.erian.ebookmark.view.Impl;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Date;

import top.erian.ebookmark.R;
import top.erian.ebookmark.model.entity.Bookmark;
import top.erian.ebookmark.presenter.SaveBookPresenter;
import top.erian.ebookmark.presenter.SaveBookmarkPresenter;
import top.erian.ebookmark.presenter.impl.SaveBookmarkPresenterImpl;
import top.erian.ebookmark.view.ISaveDataView;

public class AddBookmarkActivity extends AppCompatActivity implements ISaveDataView {

    private ProgressDialog progressDialog;
    private  String bookName;
    private Date currentTime;
    private TextView date;
    private EditText currentPage;
    private EditText note;

    public static void actionStart(Context context, String bookName) {
        Intent intent = new Intent(context, AddBookmarkActivity.class);
        intent.putExtra("bookName", bookName);
        AppCompatActivity appCompatActivity = (AppCompatActivity) context;
        appCompatActivity.startActivityForResult(intent, 1);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_bookmark);
        Toolbar toolbar = (Toolbar) findViewById(R.id.add_bookmark_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        currentTime = new Date();
        date = (TextView) findViewById(R.id.date_add_bookmark);
        currentPage = (EditText) findViewById(R.id.current_page_add_bookmark);
        note = (EditText) findViewById(R.id.note_add_bookmark);


        date.setText(currentTime.toString());
        Intent intent = getIntent();
        this.bookName = intent.getStringExtra("bookName");
    }

    @Override
    public void startSaving() {
        progressDialog = new ProgressDialog(AddBookmarkActivity.this);
        progressDialog.setTitle("Saving the bookmark");
        progressDialog.setMessage("On saving...");
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    @Override
    public void saveFailed() {
        Toast.makeText(AddBookmarkActivity.this,"Failed to save this bookmark.",
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void saveSuccess() {
        Toast.makeText(AddBookmarkActivity.this, "Success",
                Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public void saveFinished() {
        progressDialog.dismiss();
        progressDialog = null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                setResult(RESULT_CANCELED);
                finish();
                break;
            case R.id.tick:
               //TODO: save the bookmark info
                if (checkInput() == false) break;
                Bookmark bookmark = new Bookmark();
                bookmark.setCreateDate(currentTime);
                bookmark.setCurrentPage(Integer.valueOf(currentPage.getText().toString().trim()));
                bookmark.setNote(note.getText().toString());
                SaveBookmarkPresenter saveBookmarkPresenter = new SaveBookmarkPresenterImpl(this);
                saveBookmarkPresenter.saveBookmark(this.bookName, bookmark);
                setResult(RESULT_OK);
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean checkInput() {
        String currentPageStr = currentPage.getText().toString().trim();
        StringBuffer sb = new StringBuffer();

        if (currentPageStr.isEmpty()) sb.append("Current Page field need to be filled.\n");

        if (sb.length() > 0) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(AddBookmarkActivity.this)
                    .setTitle("Error")
                    .setMessage(sb.toString())
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
            dialog.show();
            sb = null;
            return false;
        }
        return true;
    }
}
