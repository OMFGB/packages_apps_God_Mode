package com.t3hh4xx0r.god_mode.widgets;

import com.t3hh4xx0r.R;
import java.util.List;

import com.t3hh4xx0r.god_mode.utils.FastBitmapDrawable;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Handler.Callback;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.provider.Settings;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.TextView;

public class ShortcutsSelectionPreference extends Preference implements OnLongClickListener {

	private static String TAG = "ShortcutSelectionPrefreence";
	
	ImageView[] mShortcuts;
	TextView[] mShortcutName;

	TextView mTitle;
	TextView mSummary;
		
	onSelectionUpdateListener mOnSelectionUpdateListener;
	onSelectionListener mOnSelectionListener;
	
	Intent[] mCustomApps;
	String[] mCustomAppNames;
	
	private String[] mCustomQuandrants = {(Settings.System.getString(this.getContext().getContentResolver(),
            Settings.System.LOCKSCREEN_CUSTOM_APP_HONEY_1)),(Settings.System.getString(this.getContext().getContentResolver(),
            Settings.System.LOCKSCREEN_CUSTOM_APP_HONEY_2)),(Settings.System.getString(this.getContext().getContentResolver(),
            Settings.System.LOCKSCREEN_CUSTOM_APP_HONEY_3)),(Settings.System.getString(this.getContext().getContentResolver(),
            Settings.System.LOCKSCREEN_CUSTOM_APP_HONEY_4))};
	
	public ShortcutsSelectionPreference(Context context){
		this(context, null);
		
	}
	
	/**
	 * A preference that has four app icons and the individual
	 * coresponding app names.
	 * 
	 * @param context
	 * @param attrs
	 */
	public ShortcutsSelectionPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
		

		 mShortcuts = new ImageView[4];
		 mShortcutName = new TextView[4];
		 mCustomAppNames = new String[4];
		 mCustomApps = new Intent[4];
		 
	
		 
		 mCustomApps = setDefaultIntents();
		
	}
	
	
	 /**
     * Creates the View to be shown for this Preference in the
     * {@link PreferenceActivity}. The default behavior is to inflate the main
     * layout of this Preference (see {@link #setLayoutResource(int)}. If
     * changing this behavior, please specify a {@link ViewGroup} with ID
     * {@link android.R.id#widget_frame}.
     * <p>
     * Make sure to call through to the superclass's implementation.
     * 
     * @param parent The parent that this View will eventually be attached to.
     * @return The View that displays this Preference.
     * @see #onBindView(View)
     */
	@Override
    protected View onCreateView(ViewGroup parent) {
        final LayoutInflater layoutInflater =
            (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        
        final View layout = layoutInflater.inflate(R.layout.preference_child, parent, false); 
   
        return layout;
    } 
	
	private interface onLongClick extends OnPreferenceClickListener {
		
		
		
		
	}
	  @Override
	  public void onBindView(View view) {
		  super.onBindView(view);
		  
			 mTitle  = (TextView) view.findViewById(R.id.title);
			 mSummary = (TextView) view.findViewById(R.id.summary);
			 if(mTitle != null && getTitle() != null)mTitle.setText(getTitle());
			 if(mSummary != null && getSummary() != null)mSummary.setText(getSummary());
				 
				 mShortcuts[0] = (ImageView) view.findViewById(R.id.short_one_icon);
				 mShortcuts[1] = (ImageView) view.findViewById(R.id.short_two_icon);
				 mShortcuts[2] = (ImageView) view.findViewById(R.id.short_three_icon);
				 mShortcuts[3] = (ImageView) view.findViewById(R.id.short_four_icon);
				 for(ImageView v: mShortcuts){
					 v.setLongClickable(true);
					 v.setOnLongClickListener(this);
				 }
				 
				 mShortcutName[0] = (TextView) view.findViewById(R.id.short_one_text);
				 mShortcutName[1] = (TextView) view.findViewById(R.id.short_two_text);
				 mShortcutName[2] = (TextView) view.findViewById(R.id.short_three_text);
				 mShortcutName[3] = (TextView) view.findViewById(R.id.short_four_text);
				 
				
		  
		  int padding = 15;
		  
		 Log.d(TAG, "Binding the view");
		  
		 Drawable[] temp = getShortcutDrawables();
		 LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		
		 for(int i =0; i < temp.length; i++){
			 
			 if(mShortcuts[i] != null){
			 Log.d(TAG, "Setting drawables"); 
			 mShortcuts[i].setBackgroundDrawable(temp[i]);
			 mShortcuts[i].setVisibility(View.VISIBLE);
			 }
			 
			 
		 }
		 
		 for(int i =0; i < temp.length; i++){

			 Log.d(TAG, "Setting titles");
			 mShortcutName[i].setText(mCustomAppNames[i]);
			 mShortcutName[i].setVisibility(View.VISIBLE);
			 
		 }
		
		  
	  }
	
	  
	  static Drawable scaledDrawable(Drawable icon,Context context, float scale) {
			final Resources resources=context.getResources();
			int sIconHeight= (int) resources.getDimension(android.R.dimen.app_icon_size);
			int sIconWidth = sIconHeight;

			int width = sIconWidth;
			int height = sIconHeight;
			Bitmap original;
			try{
			    original= Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
			} catch (OutOfMemoryError e) {
			   return icon;
			}
			Canvas canvas = new Canvas(original);
			canvas.setBitmap(original);
			icon.setBounds(0,0, width, height);
			icon.draw(canvas);
			try{
			    Bitmap endImage=Bitmap.createScaledBitmap(original, (int)(width*scale), (int)(height*scale), true);
			    original.recycle();
			    return new FastBitmapDrawable(endImage);
			} catch (OutOfMemoryError e) {
			    return icon;
			}
		    }
	   
	    
	  private Drawable[] getShortcutDrawables(){
		   
		  
		  Log.d(TAG, "Resolving drawables");
		   int numapps = 4;
		   Intent intent = new Intent();
		   PackageManager pm = this.getContext().getPackageManager();
		   mCustomApps = setDefaultIntents();
		   
		   
		 for(int i = 0; i < numapps ; i++){
				  
					try{
						intent = Intent.parseUri(mCustomQuandrants[i], 0);
						Log.d(TAG, intent.toString());
					}catch (java.net.URISyntaxException ex) {
						Log.w("", "Invalid hotseat intent: " + mCustomQuandrants[i]);
			               // bogus; leave intent=null
			        }
					catch(NullPointerException e){
						Log.w(TAG, "Maybe this is the first time laucnhing the program and setting the intents");
						intent = null;
					}
					
					 if(intent != null){
						 
						 Log.d(TAG, "Setting custom intents");
						 ResolveInfo bestMatch = pm.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY);
				         List<ResolveInfo> allMatches = pm.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
					     ComponentName com = new ComponentName(
		                        bestMatch.activityInfo.applicationInfo.packageName,
		                        bestMatch.activityInfo.name);
					     mCustomAppNames[i] = (String) bestMatch.activityInfo.applicationInfo.loadLabel(pm);
					     mCustomApps[i] = new Intent(Intent.ACTION_MAIN).setComponent(com);
					     Log.d(TAG,  mCustomApps[i].toString());
					 }
					 else{
						 Log.d(TAG, "Setting default intents");
						 ResolveInfo bestMatch = pm.resolveActivity(mCustomApps[i], PackageManager.MATCH_DEFAULT_ONLY); 
						 ComponentName com = new ComponentName(
			                        bestMatch.activityInfo.applicationInfo.packageName,
			                        bestMatch.activityInfo.name);
				         mCustomAppNames[i] = (String) bestMatch.activityInfo.applicationInfo.loadLabel(pm);
				         mCustomApps[i] = new Intent(Intent.ACTION_MAIN).setComponent(com);
				         Log.d(TAG,  mCustomApps[i].toString());
						 
					 }
			   
			   
			   
		   }
		 Drawable[] shortcutsicons = new Drawable[numapps];
		   
		  float iconScale =0.80f;
		  
		   for(int i = 0; i < numapps ; i++){
			   try {
				   Log.d(TAG, "Retreiving icons");
				   shortcutsicons[i] = pm.getActivityIcon( mCustomApps[i]);
				   shortcutsicons[i] = scaledDrawable(shortcutsicons[i], this.getContext() ,iconScale);
	           } catch (ArrayIndexOutOfBoundsException ex) {
	               Log.w("", "Missing shortcut_icons array item #" + i);
	               shortcutsicons[i] = null;
	           } catch (PackageManager.NameNotFoundException e) {
	           	//Do-Nothing
	           }
		   }
		   
		return shortcutsicons;
		   
		   
		   
	   }
	  
	  public static Intent[] setDefaultIntents(){
	    	
		  Intent[] i = {
	    			new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
	    			new Intent(android.content.Intent.ACTION_SEND).setType("text/plain").putExtra(Intent.EXTRA_EMAIL, new String("")).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
	    			new Intent(Intent.ACTION_DIAL).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
	    			new Intent(Intent.ACTION_DIAL).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)};
	    			//new Intent(Intent.ACTION_MAIN).setComponent(new ComponentName("com.android.mms","com.android.ui.ConversationList"))};
	    			//new Intent(Intent.ACTION_EDIT).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).putExtra("sms_body", "").putExtra(Intent.EXTRA_STREAM, "").setType("image/png").addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)};
	    	
	    	return i;
	    	
	    }
	  
	/**
	 * Used to callback to the prefrence to tell it to update the pictures
	 * and text that is displayed to the user.
	 * 
	 * @param l the {@link onSelectionUpdateListener} to envoke
	 * 
	 */
	public void setCallBackListener(onSelectionUpdateListener l){
		this.mOnSelectionUpdateListener = l;		
	}
	
	public void setSelectionListener(onSelectionListener l){
		this.mOnSelectionListener = l;		
	}
	
	/**
	 * 
	 * The interface to provide callbacks that allow the view
	 * to update the icons and text
	 * 
	 *
	 */
	interface onSelectionUpdateListener{
		
		void updateSelectionIcon(View v, int whichShort);
		
	}
	
	public interface onSelectionListener{
		
		int SELECTION_ONE = 0;
		int SELECTION_TWO = 1;
		int SELECTION_THREE = 2;
		int SELECTION_FOUR = 3;
		
		void startSelection(int selection);
		
	}
	
	@Override
	public boolean onLongClick(View v) {
		super.onClick();
		  
		
		if(v.equals(mShortcuts[0])){
			Log.d(TAG, "Selctor one clicked");
			mOnSelectionListener.startSelection(onSelectionListener.SELECTION_ONE);
		}
		if(v.equals(mShortcuts[1])){
			Log.d(TAG, "Selctor 2 clicked");

			mOnSelectionListener.startSelection(onSelectionListener.SELECTION_TWO);
		}
		if(v.equals(mShortcuts[2])){
			Log.d(TAG, "Selctor 3 clicked");

			mOnSelectionListener.startSelection(onSelectionListener.SELECTION_THREE);
		}
		if(v.equals(mShortcuts[3])){
			Log.d(TAG, "Selctor 4 clicked");

			mOnSelectionListener.startSelection(onSelectionListener.SELECTION_FOUR);
		}
		
		return false;
	}
	
	public void NotifyPrefrerenceChanged(){
		
		notifyChanged();
	}

}
