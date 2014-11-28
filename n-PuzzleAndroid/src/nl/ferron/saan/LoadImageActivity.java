package nl.ferron.saan;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources.NotFoundException;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class LoadImageActivity extends DrawerActivity {

	private static final int SELECT_N_PHOTOS = 100;
	static final int REQUEST_IMAGE_CAPTURE = 1;
	int numberTiles = 9;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_load_img);
		getSupportActionBar().setTitle("Choose Image");

		onCreateDialog(null).show();
		
        addImageButtons(null);
	}
	
	public Dialog onCreateDialog(Bundle savedInstanceState) {
	    AlertDialog.Builder builder = new AlertDialog.Builder(this);
	    builder.setTitle("Choose difficulty")
	           .setItems(R.array.mode, new DialogInterface.OnClickListener() {
	               public void onClick(DialogInterface dialog, int which) {
	            	   switch (which) {
			   				case 0:
			   					numberTiles = 9;
			   					break;
			   		   		case 1:		   			
			   		   			numberTiles = 16;
			   		   			break;
			   		   		case 2:		   			
			   		   			numberTiles = 25;
			   		   			break;
	   		   		
	   			}
	           }
	    });
	    return builder.create();
	}
	
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	private void addImageButtons(final Bitmap resource) {
		
		LinearLayout hsvll = (LinearLayout) findViewById(R.id.hsvLinearlayout);
	
		if (resource != null) {
			ImageButton ib = new ImageButton(this);
			
			ib.setLayoutParams(new LayoutParams(400, LayoutParams.MATCH_PARENT));
            //ib.setImageBitmap(resource);
            
            // get screen size
            int devwidth, devheight;
    		Display display = getWindowManager().getDefaultDisplay();
    		try {
    			Point size = new Point();
    			display.getSize(size);
    			devwidth = size.x;
    			devheight = size.y;
    		} catch (NoSuchMethodError e) {
    			devwidth = display.getWidth();  // deprecated
    			devheight = display.getHeight();  // deprecated
    		}
            
            final Bitmap scaledBitmap = Bitmap.createScaledBitmap(resource, devwidth, devheight/2, true);
            resource.recycle();
            ib.setImageBitmap(scaledBitmap);
            ib.setScaleType(ImageView.ScaleType.CENTER_CROP);
            ib.setBackgroundColor(Color.TRANSPARENT);
            ib.setContentDescription("Image to play the game with.");
            ib.setClickable(true);
            ib.setOnClickListener(new View.OnClickListener() {
        		@Override
    			public void onClick(View v) {        			   	       				
        			Intent myIntent = new Intent(getApplicationContext(), GameActivity.class);
        			myIntent.putExtra("numberTiles", numberTiles);
        			myIntent.putExtra("Drawable", scaledBitmap);
        			startActivity(myIntent);
        			finish();
        		};
            });       
            
            hsvll.addView(ib);
            //scaledBitmap.recycle();
            
		} else {
	        for (int i = 0; i<10; i++) {
	        	ImageButton ib = new ImageButton(this);
	        
	        	try {
		        	Drawable drawable = getResources().getDrawable(getResources()
		                    .getIdentifier("puzzle_"+i, "drawable", getPackageName()));
	        	
		        	final int drawableId =  getResources().getIdentifier("puzzle_"+i, "drawable", getPackageName());
		        	
		        	try {
		        		ib.setBackground(drawable);
		        	} catch (NoSuchMethodError e) {
		        		ib.setBackgroundDrawable(drawable);
		        	}
		        			            
		        	ib.setLayoutParams(new LayoutParams(400, LayoutParams.MATCH_PARENT));
		            ib.setScaleType(ImageView.ScaleType.CENTER_CROP);
		            ib.setContentDescription("Image to play the game with.");
		            ib.setClickable(true);
		            ib.setOnClickListener(new View.OnClickListener() {
		        		@Override
		    			public void onClick(View v) {   			        				
		        			Intent myIntent = new Intent(getApplicationContext(), GameActivity.class);
		        			myIntent.putExtra("numberTiles", numberTiles);
		        			myIntent.putExtra("imageID", drawableId);
		        			startActivity(myIntent);
		        			finish();
		        		};
		            });       
	            
		            hsvll.addView(ib);
				} catch (NotFoundException e) {
					// nothing
					        		
				}
	        }  
		}
	}

	protected void onActivityResult(int requestCode, int resultCode, 
		       Intent data) {
		
		// receive image from gallery intent
	    if (resultCode == RESULT_OK && requestCode == SELECT_N_PHOTOS) {  
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            Cursor cursor = getContentResolver().query(
                               selectedImage, filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String filePath = cursor.getString(columnIndex);
            cursor.close();

            Bitmap yourSelectedImage = BitmapFactory.decodeFile(filePath);
            addImageButtons(yourSelectedImage);
	    }
	    
	    // receive bitmap from camera intent
	    if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
	        Bitmap yourSelectedImage = (Bitmap) data.getExtras().get("data");
            addImageButtons(yourSelectedImage);
	    }
	}

	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
		 MenuInflater inflater = getMenuInflater();
	       inflater.inflate(R.menu.loadimg, menu);
	       return super.onCreateOptionsMenu(menu);
    }
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // handle item selection
	    switch (item.getItemId()) {
	        case R.id.action_camera_intent:
	        	openCameraIntent();
	            return true;
	        case R.id.action_gallery_intent:
	        	openGalleryIntent();
	            return true;
	        case R.id.action_info:
	        	showInfo();
	        	return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
	private void showInfo() {
		LayoutInflater inflater = getLayoutInflater();
		View layout = inflater.inflate(R.layout.toast_layout,
		                               (ViewGroup) findViewById(R.id.toast_layout_root));

		TextView text = (TextView) layout.findViewById(R.id.toast_text);
		text.setText("Click on an image to play the game \n" +
				"or choose your own picture");	
		
		Toast toast = new Toast(getApplicationContext());
		toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
		toast.setDuration(Toast.LENGTH_LONG);
		toast.setView(layout);
		toast.show();		
	}

	private void openCameraIntent() {
		Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
	    if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
	        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
	    }
	}
	private void openGalleryIntent() {
		Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
		photoPickerIntent.setType("image/*");
		startActivityForResult(photoPickerIntent, SELECT_N_PHOTOS);
	}
	
	

}
