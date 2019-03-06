package in.securelearning.lil.android.syncadapter.utils;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;
import android.provider.Contacts;
import android.provider.ContactsContract;
import android.widget.Toast;

import java.util.HashSet;
import java.util.Set;

import in.securelearning.lil.android.app.R;

/**
 * Created by Chaitendra on 14-Mar-18.
 */

public class ContactUtil {

    public static void saveContact(Context context, String name, String number) {
        ContentValues values = new ContentValues();
        values.put(Contacts.People.NUMBER, number);
        values.put(Contacts.People.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE);
        values.put(Contacts.People.LABEL, name);
        values.put(Contacts.People.NAME, name);
        Uri dataUri = context.getContentResolver().insert(Contacts.People.CONTENT_URI, values);
        Uri updateUri = Uri.withAppendedPath(dataUri, Contacts.People.Phones.CONTENT_DIRECTORY);
        values.clear();
        values.put(Contacts.People.Phones.TYPE, Contacts.People.TYPE_MOBILE);
        values.put(Contacts.People.NUMBER, number);
        updateUri = context.getContentResolver().insert(updateUri, values);
    }

    public static boolean isContactExistsByNumber(Context context, String number) {
        Uri lookupUri = Uri.withAppendedPath(
                ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
                Uri.encode(number));
        String[] phoneNumberProjection = {ContactsContract.PhoneLookup._ID, ContactsContract.PhoneLookup.NUMBER, ContactsContract.PhoneLookup.DISPLAY_NAME};
        Cursor cur = context.getContentResolver().query(lookupUri, phoneNumberProjection, null, null, null);
        try {
            if (cur.moveToFirst()) {
                return true;
            }
        } finally {
            if (cur != null)
                cur.close();
        }
        return false;

    }

    public static boolean isContactExistsByName(Context context, String name) {
        Uri lookupUri = Uri.withAppendedPath(
                ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
                Uri.encode(name));
        String[] nameProjection = {ContactsContract.PhoneLookup._ID, ContactsContract.PhoneLookup.DISPLAY_NAME, ContactsContract.PhoneLookup.NUMBER};
        Cursor cur = context.getContentResolver().query(lookupUri, nameProjection, null, null, null);
        try {
            if (cur.moveToFirst()) {
                return true;
            }
        } finally {
            if (cur != null)
                cur.close();
        }
        return false;

    }

    public static void makeWhatsAppCall(Context context, String number, String mimeString) {
        String displayName = null;
        String[] name = getContactNamesByNumber(context, number);
        Long id;
        ContentResolver resolver = context.getContentResolver();
        String selection = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " = ?";
        String[] selectionArguments = name;
        Cursor cursor = resolver.query(ContactsContract.Data.CONTENT_URI, new String[]{
                ContactsContract.Data._ID,
                ContactsContract.Data.DISPLAY_NAME,
                ContactsContract.Data.MIMETYPE,
                ContactsContract.CommonDataKinds.Phone.NUMBER
        }, selection, selectionArguments, ContactsContract.Contacts.DISPLAY_NAME);
        number = number.replace("+", "");
        while (cursor.moveToNext()) {
            id = cursor.getLong(cursor.getColumnIndex(ContactsContract.Data._ID));
            displayName = cursor.getString(cursor.getColumnIndex(ContactsContract.Data.DISPLAY_NAME));
            String mimeType = cursor.getString(cursor.getColumnIndex(ContactsContract.Data.MIMETYPE));
            String phoneNumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            if (mimeType.equals(mimeString) && phoneNumber.contains(number)) {

                try {
                    String data = "content://com.android.contacts/data/" + id;
                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_VIEW);
                    sendIntent.setDataAndType(Uri.parse(data), mimeString);
                    sendIntent.setPackage("com.whatsapp");
                    context.startActivity(sendIntent);
                    break;

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(context, context.getString(R.string.messageUnableToMakeWhatsAppCall), Toast.LENGTH_SHORT).show();
                }

            }
        }
    }

    public static String getContactIdByNumber(Context context, String number) {
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number));
        String _Id = null;
        boolean found = false;
        ContentResolver contentResolver = context.getContentResolver();
        Cursor contactLookup = contentResolver.query(uri, new String[]{BaseColumns._ID,
                ContactsContract.PhoneLookup.DISPLAY_NAME}, null, null, null);

        try {
            while (contactLookup != null && contactLookup.getCount() > 0 && contactLookup.moveToNext()) {

                _Id = contactLookup.getString(contactLookup.getColumnIndex(BaseColumns._ID));
                String name = contactLookup.getString(contactLookup.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
                ContentResolver resolver = context.getContentResolver();
                Cursor cursor = resolver.query(ContactsContract.Data.CONTENT_URI, null, null, null, ContactsContract.Contacts.DISPLAY_NAME);
                while (cursor.moveToNext()) {
                    _Id = cursor.getString(cursor.getColumnIndex(ContactsContract.Data._ID));
                    String displayName = cursor.getString(cursor.getColumnIndex(ContactsContract.Data.DISPLAY_NAME));
                    if (displayName.equals(name)) {
                    }


//                Cursor cursor = contentResolver.query(
//                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
//                        null,
//                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
//                        new String[]{_Id}, null);
//
//                while (cursor.moveToNext()) {
//                    String mimeType = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.MIMETYPE));
//                    if (mimeType.equals(mimeString)) {
//                        found = true;
//                        break;
//                    }
//                }
//
//
//                cursor.close();
//                if (found) {
//                    break;
//                }
//                String mimeType = contactLookup.getString(contactLookup.getColumnIndex(ContactsContract.Data.MIMETYPE));
//                if (mimeType.equals(mimeString)) {
//                    break;
//                }
                }
            }
        } finally {
            if (contactLookup != null) {
                contactLookup.close();
            }
        }

        return _Id;
    }

    public static String[] getContactNamesByNumber(Context context, String number) {
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number));
        String displayName = null;
        ContentResolver contentResolver = context.getContentResolver();
        Cursor contactLookup = contentResolver.query(uri, new String[]{BaseColumns._ID,
                ContactsContract.PhoneLookup.DISPLAY_NAME}, null, null, null);
        Set<String> names = new HashSet<String>();

        try {
            while (contactLookup != null && contactLookup.getCount() > 0 && contactLookup.moveToNext()) {
                displayName = contactLookup.getString(contactLookup.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
                names.add(displayName);
            }

        } finally {
            if (contactLookup != null) {
                contactLookup.close();
            }
        }

        return names.toArray(new String[]{});
    }

    public static void sendEmail(Context context, String emailId, String subject) {
        Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                context.getString(R.string.labelMailTo), emailId, null));
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, "");
        context.startActivity(Intent.createChooser(intent, context.getString(R.string.messageChooseEmailClient)));
    }

    @SuppressLint("MissingPermission")
    public static void makeCellularCall(Context context, String number) {
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:" + number));
        context.startActivity(callIntent);
    }

}
