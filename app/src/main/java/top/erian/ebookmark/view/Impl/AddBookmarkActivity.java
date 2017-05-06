package top.erian.ebookmark.view.Impl;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import top.erian.ebookmark.R;
import top.erian.ebookmark.view.ISaveDataView;

public class AddBookmarkActivity extends AppCompatActivity implements ISaveDataView {

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_bookmark);
        Toolbar toolbar = (Toolbar) findViewById(R.id.add_bookmark_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
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
                finish();
                break;
            case R.id.tick:
               //TODO: save the bookmark info
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
