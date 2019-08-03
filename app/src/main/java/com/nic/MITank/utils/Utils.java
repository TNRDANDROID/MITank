package com.nic.MITank.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.SwitchCompat;
import androidx.core.content.FileProvider;


import com.nic.MITank.BuildConfig;
import com.nic.MITank.R;
import com.nic.MITank.application.NICApplication;
import com.nic.MITank.constant.AppConstant;
import com.nic.MITank.session.PrefManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;


public class Utils {

    private static final String SHARED_PREFERENCE_UTILS = "Nimble";
    private static final int SECONDS_IN_A_MINUTE = 60;
    private static final int MINUTES_IN_AN_HOUR = 60;
    private static SharedPreferences sharedPreferences;
    private static final int SECOND_MILLIS = 1000;
    private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final int DAY_MILLIS = 24 * HOUR_MILLIS;
    private static String CIPHER_NAME = "AES/CBC/PKCS5PADDING";
    private static int CIPHER_KEY_LEN = 16; //128 bits
    private static PrefManager prefManager;

    private static void initializeSharedPreference() {
        sharedPreferences = NICApplication.getGlobalContext()
                .getSharedPreferences(SHARED_PREFERENCE_UTILS,
                        Context.MODE_PRIVATE);
    }

    public static boolean isOnline() {
        boolean status = false;
        ConnectivityManager cm = (ConnectivityManager) NICApplication.getGlobalContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null) { // connected to the internet
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                status = true;
            } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                status = true;
            }
        } else {
            status = false;
        }

        return status;
    }

    public static Boolean isValidEmail(String email) {
        Boolean status = true;
        String mail[] = email.split(",");
        for (int i = 0; i < mail.length; i++) {
            if (!validateMail(mail[i])) {
                return false;
            }
        }
        return status;
    }

    public static boolean validateMail(String email) {
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        if (email.matches(emailPattern)) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isEmailValid(String email) {

        boolean flag;
        String expression = "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" + "\\@"
                + "[a-zA-Z0-9][a-zA-Z0-9\\-]{1,64}" + "(" + "\\."
                + "[a-zA-Z0-9][a-zA-Z0-9\\-]{1,25}" + ")+";

        CharSequence inputStr = email.trim();
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);

        flag = matcher.matches();
        return flag;

    }

//    public static void showAlert(Context context, String message) {
//        try {
//            AlertDialog.Builder builder = new AlertDialog.Builder(context);
//            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//            @SuppressLint("InflateParams") View dialogView = inflater.inflate(R.layout.alert_dialog,null);
//            final AlertDialog alertDialog = builder.create();
//            alertDialog.setView(dialogView, 0, 0, 0, 0);
//            alertDialog.setCancelable(false);
//            alertDialog.show();
//
//            MyCustomTextView tv_message = (MyCustomTextView) dialogView.findViewById(R.id.tv_message);
//            tv_message.setText(message);
//
//            Button btnOk = (Button) dialogView.findViewById(R.id.btn_ok);
//            btnOk.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    alertDialog.dismiss();
//                }
//            });
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    public static void showAlert(Activity activity, String msg){
        try {
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.alert_dialog);

       TextView text = (TextView) dialog.findViewById(R.id.tv_message);
        text.setText(msg);

        Button dialogButton = (Button) dialog.findViewById(R.id.btn_ok);
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static long getDateInMillis(String srcDate) {
        SimpleDateFormat desiredFormat = new SimpleDateFormat(
                "MM/dd/yyyy HH:mm:ss");
        long dateInMillis = 0;
        try {
            Date date = desiredFormat.parse(srcDate);
            dateInMillis = date.getTime();
            return dateInMillis;
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return 0;
    }




    public static double getScreenInch(Activity activity) {
        DisplayMetrics dm = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        int dens = dm.densityDpi;
        double wi = (double) width / (double) dens;
        double hi = (double) height / (double) dens;
        double x = Math.pow(wi, 2);
        double y = Math.pow(hi, 2);
        double screenInches = Math.sqrt(x + y);
        return screenInches;
    }



    public static boolean isValidMobile(String phone2) {
        boolean check;
        check = phone2.length() == 10;
        return check;
    }

    public static String emailOrNumberValues(List<String> values) {
        String listValues = "";
        StringBuilder builder = new StringBuilder();
        String prefix = "";
        for (String number : values) {
            if (values.size() > 1) {
                builder.append(prefix);
                prefix = ",";
                builder.append(number);
            } else {
                builder.append(number);
            }
        }
        listValues = builder.toString();
        return listValues;
    }



    public static String enableSwitchValues(SwitchCompat enableSwitch) {
        String switchValue = "";
        if (enableSwitch.isChecked()) {
            switchValue = "on";
        } else {
            switchValue = "";
        }
        return switchValue;
    }




    //Version name
    public static String getVersionName(Context context) {
        PackageManager manager = context.getPackageManager();
        PackageInfo info = null;
        try {
            info = manager.getPackageInfo(
                    context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        String version = info.versionName;
        return version;
    }




    private static Bitmap screenShot(View view) {
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }

    private static File saveBitmap(Bitmap bm, String fileName) {
        final String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/IrisReport";
        File dir = new File(path);
        if (!dir.exists())
            dir.mkdirs();
        File oldFile = new File(new File(path), fileName);
        if (oldFile.exists())
            oldFile.delete();
        File file = new File(dir, fileName);
        try {
            FileOutputStream fOut = new FileOutputStream(file);
            bm.compress(Bitmap.CompressFormat.PNG, 90, fOut);
            fOut.flush();
            fOut.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file;
    }

    public static void captureMapScreen(final Activity activity, final View mView, ImageView imageView) {
        Bitmap bitmap = Bitmap.createBitmap(mView.getWidth(), mView.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        mView.draw(canvas);
        imageView.setImageDrawable(null);
        shareScreenShot(activity, bitmap);

    }

    public static void shareScreenShot(Activity activity, View view) {
        Bitmap bm = screenShot(view);
        shareScreenShot(activity, bm);
    }

    public static void shareScreenShot(Activity activity, Bitmap bm) {
        File file = saveBitmap(bm, "IrisReport.png");   /*change Uri.fromfile to Fileprovider.getUriForFile becoz the old method is not Supported for Android 8 so permission must be added in manifest file*/
//        Uri uri = Uri.fromFile(new File(file.getAbsolutePath()));
        Uri uri = FileProvider.getUriForFile(activity,
                BuildConfig.APPLICATION_ID + ".provider",
                new File(file.getAbsolutePath()));
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_TEXT, "");
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
        shareIntent.setType("image/*");
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        activity.startActivity(Intent.createChooser(shareIntent, "share via"));
    }


    public static String randomChar() {
        char[] chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".toCharArray();
        Random rnd = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 15; i++)
            sb.append(chars[rnd.nextInt(chars.length)]);
        Log.d("rand", sb.toString());
        return sb.toString();
    }

    public static String getSHA(String UserPassWord) {
        try {

            // Static getInstance method is called with hashing SHA
            MessageDigest md = MessageDigest.getInstance("SHA-256");

            // digest() method called
            // to calculate message digest of an input
            // and return array of byte
            byte[] messageDigest = md.digest(UserPassWord.getBytes());

            // Convert byte array into signum representation
            BigInteger no = new BigInteger(1, messageDigest);

            // Convert message digest into hex value
            String hashtext = no.toString(16);

            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }

            return hashtext;
        }

        // For specifying wrong message digest algorithms
        catch (NoSuchAlgorithmException e) {
            System.out.println("Exception thrown"
                    + " for incorrect algorithm: " + e);

            return null;
        }
    }

    public static final String md5(final String s) {
        final String MD5 = "MD5";
        try {
            // Create MD5 Hash
            MessageDigest digest = MessageDigest
                    .getInstance(MD5);
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuilder hexString = new StringBuilder();
            for (byte aMessageDigest : messageDigest) {
                String h = Integer.toHexString(0xFF & aMessageDigest);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }


    public static String encrypt(String key, String iv, String data) {

        try {
            IvParameterSpec ivSpec = new IvParameterSpec(iv.getBytes("UTF-8"));
            SecretKeySpec secretKey = new SecretKeySpec(fixKey(key).getBytes("UTF-8"), "AES");

            Cipher cipher = Cipher.getInstance(CIPHER_NAME);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec);

            byte[] encryptedData = cipher.doFinal((data.getBytes()));

            String encryptedDataInBase64 = android.util.Base64.encodeToString(encryptedData, 0);
            String ivInBase64 = android.util.Base64.encodeToString(iv.getBytes("UTF-8"), 0);

            return encryptedDataInBase64 + ":" + ivInBase64;

        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }


    }

    private static String fixKey(String key) {

        if (key.length() < CIPHER_KEY_LEN) {
            int numPad = CIPHER_KEY_LEN - key.length();

            for (int i = 0; i < numPad; i++) {
                key += "0"; //0 pad to len 16 bytes
            }

            return key;

        }

        if (key.length() > CIPHER_KEY_LEN) {
            return key.substring(0, CIPHER_KEY_LEN); //truncate to 16 bytes
        }

        return key;
    }

    /**
     * Decrypt data using AES Cipher (CBC) with 128 bit key
     *
     * @param key  - key to use should be 16 bytes long (128 bits)
     * @param data - encrypted data with iv at the end separate by :
     * @return decrypted data string
     */


    public static String decrypt(String key, String data) {

        try {
            String[] parts = data.split(":");

            IvParameterSpec iv = new IvParameterSpec(android.util.Base64.decode(parts[1], 1));
            // System.out.println(fixKey(iv));
            SecretKeySpec secretKey = new SecretKeySpec(fixKey(key).getBytes("UTF-8"), "AES");

            Cipher cipher = Cipher.getInstance(CIPHER_NAME);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, iv);

            byte[] decodedEncryptedData = android.util.Base64.decode(parts[0], 1);

            byte[] original = cipher.doFinal(decodedEncryptedData);

            return new String(original);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public static JSONObject villageListDistrictBlockWiseJsonParams(Activity activity) throws JSONException {
        prefManager = new PrefManager(activity);
        JSONObject dataSet = new JSONObject();
        dataSet.put(AppConstant.KEY_SERVICE_ID, AppConstant.KEY_VILLAGE_LIST_DISTRICT_BLOCK_WISE);
        dataSet.put(AppConstant.DISTRICT_CODE, prefManager.getDistrictCode());
        dataSet.put(AppConstant.BLOCK_CODE, prefManager.getBlockCode());
        Log.d("villageListDistBlock", "" + dataSet);
        return dataSet;
    }

    public static JSONObject HabitationListDistrictBlockVillageWiseJsonParams(Activity activity) throws JSONException {
        prefManager = new PrefManager(activity);
        JSONObject dataSet = new JSONObject();
        dataSet.put(AppConstant.KEY_SERVICE_ID, AppConstant.KEY_HABITATION_LIST_DISTRICT_BLOCK_WISE);
        dataSet.put(AppConstant.DISTRICT_CODE, prefManager.getDistrictCode());
        dataSet.put(AppConstant.BLOCK_CODE, prefManager.getBlockCode());
        Log.d("HabListDistBlock", "" + dataSet);
        return dataSet;
    }

    public static JSONObject tankPondListJsonParams() throws JSONException {
        JSONObject dataSet = new JSONObject();
        dataSet.put(AppConstant.KEY_SERVICE_ID, AppConstant.KEY_MI_TANK_DATA);
        Log.d("object", "" + dataSet);
        return dataSet;
    }

    public static JSONObject tankPondStructureListJsonParams() throws JSONException {
        JSONObject dataSet = new JSONObject();
        dataSet.put(AppConstant.KEY_SERVICE_ID, AppConstant.KEY_MI_TANK_STRUCTURE);
        Log.d("object", "" + dataSet);
        return dataSet;
    }

    public static JSONObject tankConditionListJsonParams() throws JSONException {
        JSONObject dataSet = new JSONObject();
        dataSet.put(AppConstant.KEY_SERVICE_ID, AppConstant.KEY_MI_TANK_CONDITION);
        Log.d("object", "" + dataSet);
        return dataSet;
    }
}
