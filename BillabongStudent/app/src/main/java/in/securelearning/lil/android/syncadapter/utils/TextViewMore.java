package in.securelearning.lil.android.syncadapter.utils;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.StyleSpan;
import android.text.util.Linkify;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.xml.sax.XMLReader;

import in.securelearning.lil.android.app.R;
import in.securelearning.lil.android.base.customchrometabutils.LinkTransformationMethod;

/**
 * Created by Chaitendra on 4/4/2017.
 */

public class TextViewMore {

    public static CharSequence viewMore(final String stringData, final TextView textView, final TextView viewMoreLessTextView) {
        try {
            final CharSequence[] strTruncateText = new CharSequence[1];
            String first;
            SpannableString spannableString;
            Spanned htmlDescription = Html.fromHtml(stringData);
            String stringWithOutExtraSpace = new String(htmlDescription.toString()).trim();
            final CharSequence string = htmlDescription.subSequence(0, stringWithOutExtraSpace.length());
            if (string.length() > 200) {
                strTruncateText[0] = string;
                strTruncateText[0] = strTruncateText[0].subSequence(0, 200) + "...";
                textView.setText(strTruncateText[0]);
                viewMoreLessTextView.setVisibility(TextView.VISIBLE);
                viewMoreLessTextView.setText("Read more");
                viewMoreLessTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (viewMoreLessTextView.getText().toString().trim().equals("Read more")) {
                            textView.setText(string);
                            viewMoreLessTextView.setText("Read less");

                        } else {
                            strTruncateText[0] = string.toString();
                            strTruncateText[0] = strTruncateText[0].subSequence(0, 200) + "...";
                            textView.setText(strTruncateText[0]);
                            viewMoreLessTextView.setText("Read more");
                        }

                    }
                });
                return strTruncateText[0];
            } else {
                textView.setText(string);
                viewMoreLessTextView.setVisibility(TextView.GONE);
                return string;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static void setPostText(final Context context, final String postText, final TextView textView, final TextView viewMoreLessTextView) {

        //String message = (postText).replaceAll("\\n", "<br>");
        /*Convert text to spanned for to maintain html format*/
        Spanned html = Html.fromHtml(postText);

        /*Trim training white space from spanned text*/
        String stringWithOutExtraSpace = html.toString().trim();

        /*Convert to CharSequence and removed whitespace from at the end*/
        CharSequence charSequence = html.subSequence(0, stringWithOutExtraSpace.length());

        /*Again get CharSequence value in spanned*/
        html = (Spanned) charSequence;

        /*Make links highlighted and clickable*/
        textView.setTransformationMethod(new LinkTransformationMethod(context, Linkify.ALL, R.color.colorLearningNetworkPrimary));
        textView.setMovementMethod(LinkMovementMethod.getInstance());

        /*Get list of urls from generic method*/
        //ArrayList<String> urls = new ArrayList<>(Arrays.asList(GeneralUtils.getArrayOfAllUrls(String.valueOf(html))));

        /*Creating SpannableString from html(spanned) to customize html text*/
        SpannableString customSpannableString = new SpannableString(html);

        // for (String url : urls) {
        /*Here changing size of url text*/
        //      String updatedUrl = url.trim();
        //     customSpannableString.setSpan(new RelativeSizeSpan(0.5f), html.toString().indexOf(updatedUrl), html.toString().indexOf(updatedUrl) + url.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        //  }

        /*Getting hashTags from getHashTags()*/
        final SpannableString postSpannableString = getHashTags(context, customSpannableString);

        textView.setText(postSpannableString, TextView.BufferType.SPANNABLE);

        if (postSpannableString.length() > 200) {

            // String truncate = string.toString().substring(0, 200) + context.getString(R.string.stringTripleDot);
            //final SpannableString lessString = getHashTags(context, truncate);

            textView.setEllipsize(TextUtils.TruncateAt.END);
            textView.setMaxLines(4);
            viewMoreLessTextView.setVisibility(TextView.VISIBLE);
            viewMoreLessTextView.setText(context.getString(R.string.labelReadMore));

            viewMoreLessTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (viewMoreLessTextView.getText().toString().trim().equals(context.getString(R.string.labelReadMore))) {
                        textView.setText(postSpannableString, TextView.BufferType.SPANNABLE);
                        textView.setEllipsize(null);
                        textView.setMaxLines(Integer.MAX_VALUE);
                        viewMoreLessTextView.setText(context.getString(R.string.labelReadLess));
                    } else {
                        textView.setText(postSpannableString, TextView.BufferType.SPANNABLE);
                        textView.setEllipsize(TextUtils.TruncateAt.END);
                        textView.setMaxLines(4);
                        viewMoreLessTextView.setText(context.getString(R.string.labelReadMore));
                    }

                }
            });
        } else {
            viewMoreLessTextView.setVisibility(TextView.GONE);
        }
    }


    private static SpannableString getHashTags(final Context context, SpannableString stringData) {
        final SpannableString string = new SpannableString(stringData);

        int start = -1;
        for (int i = 0; i < stringData.length(); i++) {
            if (stringData.charAt(i) == '#') {
                start = i;
            } else if (stringData.charAt(i) == ' ' || (i == stringData.length() - 1 && start != -1)) {
                if (start != -1) {
                    if (i == stringData.length() - 1) {
                        i++;
                    }

                    final String tag = String.valueOf(stringData).substring(start, i);
                    string.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), start, i, Spannable.SPAN_INCLUSIVE_INCLUSIVE);

                    string.setSpan(new ClickableSpan() {

                        @Override
                        public void onClick(@NonNull View widget) {
                            Log.e("Hash", String.format("Clicked", tag));

                        }

                        @Override
                        public void updateDrawState(TextPaint ds) {
                            ds.setColor(ContextCompat.getColor(context, R.color.colorLearningNetworkPrimary));
                            ds.setUnderlineText(false);

                        }

                    }, start, i, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    start = -1;
                }
            }
        }

        return string;
    }

    public static void viewMoreLearningOutcomes(final CharSequence stringExpanded, final CharSequence stringCollapsed, final TextView textView, final TextView mViewMoreLessTextView) {

        if (stringExpanded.length() > 200) {
            textView.setText(stringExpanded);
            textView.setMaxLines(3);
            textView.setEllipsize(TextUtils.TruncateAt.END);
            mViewMoreLessTextView.setVisibility(TextView.VISIBLE);
            mViewMoreLessTextView.setText("Read more");
            mViewMoreLessTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mViewMoreLessTextView.getText().toString().trim().equals("Read more")) {
                        textView.setText(stringExpanded);
                        textView.setMaxLines(Integer.MAX_VALUE);
                        mViewMoreLessTextView.setText("Read less");

                    } else {
                        textView.setText(stringExpanded);
                        textView.setMaxLines(3);
                        textView.setEllipsize(TextUtils.TruncateAt.END);
                        mViewMoreLessTextView.setText("Read more");
                    }

                }
            });
        } else {
            textView.setText(stringExpanded);
            textView.setMaxLines(Integer.MAX_VALUE);
            mViewMoreLessTextView.setVisibility(TextView.GONE);
        }

    }

    public static CharSequence viewMoreSpannable(final String stringData, final TextView textView, final TextView mViewMoreLessTextView) {
        final String[] strTruncateText = new String[1];
        final String[] first = new String[1];
        SpannableString spannableString;
        Spanned htmlDescription = Html.fromHtml(stringData);
        String descriptionWithOutExtraSpace = new String(htmlDescription.toString()).trim();
        final CharSequence string = htmlDescription.subSequence(0, descriptionWithOutExtraSpace.length());
        if (string.length() > 200) {
            strTruncateText[0] = string.toString();
            strTruncateText[0] = strTruncateText[0].substring(0, 200) + "..." + " View more";
            textView.setText(strTruncateText[0]);
            spannableString = new SpannableString(strTruncateText[0]);
            first[0] = " View more";
            int firstIndex = spannableString.toString().indexOf(first[0]);

            ClickableSpan clickable_text = new ClickableSpan() {
                @Override
                public void onClick(View widget) {
                    if (first[0].equals(" View more")) {
                        textView.setText(string + " View less");
                        first[0] = " View less";
                    } else {
                        strTruncateText[0] = string.toString();
                        strTruncateText[0] = strTruncateText[0].substring(0, 200) + "..." + " View more";
                        textView.setText(strTruncateText[0]);
                        first[0] = " View more";
                    }

                }

                @Override
                public void updateDrawState(TextPaint ds) {
                    super.updateDrawState(ds);
                    ds.setUnderlineText(false);
                }
            };

            spannableString.setSpan(clickable_text, firstIndex, firstIndex + first[0].length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            textView.setText(spannableString);
            textView.setMovementMethod(LinkMovementMethod.getInstance());
            textView.setHighlightColor(Color.BLUE);

            return strTruncateText[0];
        } else {
            textView.setText(string);
            mViewMoreLessTextView.setVisibility(TextView.GONE);
            return string;
        }
    }

    public static CharSequence getPlaintext(String string) {
        Spanned spannedText = Html.fromHtml(string);
        String textWithOutExtraSpace = new String(spannedText.toString()).trim();
        CharSequence plainText = spannedText.subSequence(0, textWithOutExtraSpace.length());
        return plainText;
    }


    public static String removeSpaceTags(String string) {
        String b1 = string.replaceAll("</br>", "");
        return b1.replaceAll("<p>", "");
    }

    public static String stripHtml(String html) {
        String value = html.replaceAll("<.*?>", "");
        return value.replaceAll("&nbsp;", " ").replaceAll("&[a-zA-Z0-9]+;", "").trim();
    }

    public static String stripHtmlForQuiz(String html) {
        return html.replaceAll("&nbsp;", " ").replaceAll("&[a-zA-Z0-9]+;", "").trim();
    }

    public static void copyTextToClipboard(Context context, String text) {
        int sdk = android.os.Build.VERSION.SDK_INT;
        if (sdk < android.os.Build.VERSION_CODES.HONEYCOMB) {
            android.text.ClipboardManager clipboard = (android.text.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            clipboard.setText(stripHtml(text));
        } else {
            android.content.ClipboardManager clipboard = (android.content.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            android.content.ClipData clip = android.content.ClipData.newPlainText(context.getString(R.string.messageTextCopied), stripHtml(text));
            clipboard.setPrimaryClip(clip);
        }
        Toast.makeText(context, context.getString(R.string.messageTextCopied), Toast.LENGTH_SHORT).show();
    }

    public static String capitalizeFirstLetterOfWord(String content) {
        String[] strArray = content.split(" ");
        StringBuilder builder = new StringBuilder();
        for (String s : strArray) {
            String cap = s.substring(0, 1).toUpperCase() + s.substring(1);
            builder.append(cap + " ");
        }
        return builder.toString();
    }

//    public static String removeHttp(String commentStr) {
//        String urlPattern = "((https?|ftp|gopher|telnet|file|Unsure|http):((//)|(\\\\))+[\\w\\d:#@%/;$()~_?\\+-=\\\\\\.&]*)";
//        Pattern p = Pattern.compile(urlPattern, Pattern.CASE_INSENSITIVE);
//        Matcher m = p.matcher(commentStr);
//        int i = 0;
//        while (m.find()) {
//            commentStr = commentStr.replaceAll(m.group(i), "").trim();
//            i++;
//        }
//        return commentStr;
//    }

    public static class UlTagHandler implements Html.TagHandler {
        @Override
        public void handleTag(boolean opening, String tag, Editable output,
                              XMLReader xmlReader) {
            if (tag.equals("ul") && !opening) output.append("\n");
            if (tag.equals("li") && opening) output.append("\n\t•");
        }
    }
}
