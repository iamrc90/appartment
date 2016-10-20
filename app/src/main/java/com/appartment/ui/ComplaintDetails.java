package com.appartment.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.appartment.R;
import com.appartment.app.AppConfig;
import com.appartment.helpers.SessionManager;
import com.appartment.helpers.Utils;
import com.appartment.model.Ticket;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ComplaintDetails extends AppCompatActivity implements View.OnLongClickListener{
    private final String TAG = ComplaintDetails.class.getSimpleName();
    private TextView mPriority, mTicketNumber, mTicketDate, mSummary, mAddress;
    private final int IMAGE_CAPTURE_REQ_CODE = 8282;
    private String imageEncoded;
    private List<String> imagesEncodedList;
    private Ticket ticketDetail;
    private EditText mComment;
    private Button mResloveBtn;
    private SessionManager session;
    private HorizontalScrollView hScrollView;
    private LinearLayout imageLayout;
    private String mCurrentPhotoPath;
    private ProgressDialog progressDialog;
    private String commentBody;
    private Spinner spinner;
    private String[] ticketActions;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complaint_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // get layout for imageViews
        hScrollView = (HorizontalScrollView) findViewById(R.id.imageScroller);
        imageLayout = (LinearLayout) findViewById(R.id.imageList);
//        spinner = (Spinner) findViewById(R.id.status_spinner);
        session = new SessionManager(getApplicationContext());
        // bind view
        mPriority = (TextView) findViewById(R.id.priority);
        mTicketNumber = (TextView) findViewById(R.id.ticketNumber);
        mTicketDate = (TextView) findViewById(R.id.ticketDate);
        mSummary = (TextView) findViewById(R.id.summary);
        mAddress = (TextView) findViewById(R.id.address);
        mResloveBtn = (Button) findViewById(R.id.btnResolved);
        mComment = (EditText) findViewById(R.id.input_commment);
        ticketDetail = (Ticket) getIntent().getSerializableExtra(ComplaintsActivity.serialisedObjKey);

        mPriority.setText(ticketDetail.getPriority().toString());
        mTicketNumber.setText(ticketDetail.getTicketNumber().toString());
        mTicketDate.setText(ticketDetail.getTicketDate().toString());
        mSummary.setText(ticketDetail.getSummary().toString());
        mAddress.setText(ticketDetail.getAddress().toString());

        // set color for priority
        mPriority.setBackgroundColor(Color.parseColor(AppConfig.getPriorityBackground(ticketDetail.getPriority())));
        mPriority.setTextColor(Color.parseColor(AppConfig.getPriorityTextColor(ticketDetail.getPriority())));

        // mark the ticket as resolved.
        mResloveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!validate()) {
                    return;
                }
                if(imagesEncodedList != null && imagesEncodedList.size() > AppConfig.PHOTO_CAPTURE_LIMIT) {
                    Toast.makeText(ComplaintDetails.this,"Max" + AppConfig.PHOTO_CAPTURE_LIMIT + "images can be uploaded at once.",Toast.LENGTH_LONG).show();
                    return;
                }
                    upload();
            }
        });

//        //setting data to spinner
//        ticketActions = getResources().getStringArray(R.array.ticket_actions);
//        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,
//                android.R.layout.simple_dropdown_item_1line, ticketActions);
//        spinner.setAdapter(arrayAdapter);
//        spinner.setBackgroundColor();
    }

    public boolean validate() {
        boolean valid = true;

        String comment = mComment.getText().toString();

        if (comment.isEmpty()) {
            mComment.setError("should not be blank");
            valid = false;
        } else {
            mComment.setError(null);
        }
        return valid;
    }

    private void captureImage() {
        if(imagesEncodedList == null) {
            imagesEncodedList = new ArrayList<>();
        }
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                ex.printStackTrace();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.appartment.ui.ComplaintDetails",
                        photoFile);
                takePictureIntent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, IMAGE_CAPTURE_REQ_CODE);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == IMAGE_CAPTURE_REQ_CODE && resultCode != RESULT_CANCELED) {
            // set scroller to visible
            showImageScroller();
            setPic();
        }
    }
    private void upload(){
        commentBody = mComment.getText().toString();
        new OkHttpImageUploader().execute(imagesEncodedList);
    }

    @Override
    public boolean onLongClick(final View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(this.getResources().getString(R.string.alert_remove_image))
                .setCancelable(true)
                .setNegativeButton(this.getResources().getString(R.string.alert_btn_no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setMessage(this.getResources().getString(R.string.are_you_sure_txt))
                .setPositiveButton(this.getResources().getString(R.string.alert_btn_yes), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // remove the view and remove path from imagesEncodedList
                        imagesEncodedList.remove((String)view.getTag());
                        ViewGroup parentView = (ViewGroup) view.getParent();
                        parentView.removeView(view);
                        if(imagesEncodedList.size() <= 0) {
                            hScrollView.setVisibility(View.GONE);
                        }
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
        return true;
    }

    private class OkHttpImageUploader extends AsyncTask<List<String>, Void, JSONObject> {
        private JSONObject jsonRes;
        @Override
        protected JSONObject doInBackground(List<String>... params) {
            try {
                MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
                int filesCount = 0;
                if(params[0] != null) {
                    for (String imagePath : params[0]) {
                        File sourceFile = new File(imagePath);
                        filesCount++;
                        // compress file
                        Bitmap bmp = BitmapFactory.decodeFile(imagePath);
                        ByteArrayOutputStream bos = new ByteArrayOutputStream();
                        bmp.compress(Bitmap.CompressFormat.JPEG, 40, bos);
                        String mime = getImageMimeType(imagePath);
                        MediaType MEDIA_TYPE = MediaType.parse(mime);
                        builder.addFormDataPart("files_"+filesCount,sourceFile.getName(),RequestBody.create(MEDIA_TYPE, bos.toByteArray()));
                    }
                }

                builder.addFormDataPart("totalFiles",filesCount+"");
                builder.addFormDataPart("ticket_id",ticketDetail.getTicketNumber());
                builder.addFormDataPart("user_id",Integer.toString(session.getUserId()));
                builder.addFormDataPart("ticket_comments",commentBody);
                RequestBody requestBody = builder.build();
                Request request = new Request.Builder()
                        .url(AppConfig.URL_UPLOAD_DATA)
                        .post(requestBody)
                        .build();

                OkHttpClient client = new OkHttpClient();
                Response response = client.newCall(request).execute();
                jsonRes =  new JSONObject(response.body().string());

            } catch (UnknownHostException | UnsupportedEncodingException e) {
                Log.e(TAG, "Error: " + e.getLocalizedMessage());
            } catch (Exception e) {
                Log.e(TAG, "Other Error: " + e.getLocalizedMessage());
            }
            return jsonRes;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(ComplaintDetails.this);
            progressDialog.setCancelable(false);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage(getResources().getString(R.string.submit_data_loader_txt));
            showDialog();
        }

        @Override
        protected void onPostExecute(JSONObject res) {
            super.onPostExecute(res);
            hideDialog();
            if(res == null) {
                Toast.makeText(ComplaintDetails.this,"Server not responding. Please try again later.",Toast.LENGTH_SHORT).show();
            } else try {
                if(res.getInt("status") == 200) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ComplaintDetails.this);
                    builder.setTitle(getResources().getString(R.string.upload_success))
                            .setCancelable(false)
                            .setMessage(getResources().getString(R.string.upload_success_msg))
                            .setPositiveButton(getResources().getString(R.string.alert_btn_ok), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.dismiss();
                                    Intent intent = new Intent(ComplaintDetails.this, ListTickets.class);
                                    startActivity(intent);
                                    finish();
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();
                } else if(res.getInt("status") == 400){
                    Toast.makeText(ComplaintDetails.this,res.getString("message"),Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        private String getImageMimeType(String imagePath) {
            String type = null;
            if(imagePath.lastIndexOf(".") != -1) {
                String ext = imagePath.substring(imagePath.lastIndexOf(".")+1);
                MimeTypeMap mime = MimeTypeMap.getSingleton();
                type = mime.getMimeTypeFromExtension(ext);
            }
            return type;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.complaint_detail_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.logout:
                logout();
                return true;
            case R.id.attachment:
                captureImage();
                return true;
        }
        return false;
    }

    private void logout() {
        session.setLogin(false,0);
        Intent intent = new Intent(ComplaintDetails.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
        finish();
    }

    private void showImageScroller(){
        hScrollView.setVisibility(View.VISIBLE);
    }

    private void hideImageScroller(){
        hScrollView.setVisibility(View.GONE);
    }

    private void setPic() {
        // Get the dimensions of the View
        ImageView imageView = createImageView();
        imageView.setOnLongClickListener(this);
        imageView.setTag(mCurrentPhotoPath);
        int targetW = imageView.getMaxWidth();
        int targetH = imageView.getMaxHeight();

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        // save file path for future uploading purpose
        imageView.setImageBitmap(bitmap);
        imagesEncodedList.add(mCurrentPhotoPath);
        imageLayout.addView(imageView);
        mCurrentPhotoPath = null;
    }


    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private ImageView createImageView() {
        ImageView imageView = new ImageView(this);
        float scale = getResources().getDisplayMetrics().density;
        int dpWidthInPx  = (int) (100 * scale);
        int dpHeightInPx = (int) (150 * scale);
        imageView.setMaxWidth(dpWidthInPx);
        imageView.setMaxHeight(dpHeightInPx);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(dpWidthInPx,dpHeightInPx);
        params.setMarginEnd((int) (2 * scale));
        imageView.setLayoutParams(params);
        return imageView;
    }

    private void showDialog() {
        if (!progressDialog.isShowing())
            progressDialog.show();
    }

    private void hideDialog() {
        if (progressDialog.isShowing())
            progressDialog.dismiss();
    }
}
