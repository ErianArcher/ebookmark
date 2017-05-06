package top.erian.ebookmark.view.Impl;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import top.erian.ebookmark.R;
import top.erian.ebookmark.view.ISaveDataView;

public class AddBookActivity extends AppCompatActivity implements ISaveDataView {

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_book);
        Toolbar toolbar = (Toolbar) findViewById(R.id.add_book_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    @Override
    public void startSaving() {
        progressDialog = new ProgressDialog(AddBookActivity.this);
        progressDialog.setTitle("Saving book");
        progressDialog.setMessage("On saving...");
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    @Override
    public void saveFailed() {
        Toast.makeText(AddBookActivity.this, "Fail to save...",
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void saveSuccess() {
        Toast.makeText(AddBookActivity.this, "Save successfully...",
                Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(AddBookActivity.this, MainActivity.class);
        // Broadcast to notify mainactivity to update booklist
        startActivity(intent);
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
               //TODO: save the book info
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
