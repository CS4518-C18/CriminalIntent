package com.bignerdranch.android.criminalintent;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.FileProvider;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.google.android.gms.vision.face.FaceDetector;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.UUID;

import static com.bignerdranch.android.criminalintent.Utilities.detectFaces;
import static com.bignerdranch.android.criminalintent.Utilities.getFaceDetector;
import static com.bignerdranch.android.criminalintent.Utilities.scaleDown;
import static com.bignerdranch.android.criminalintent.Utilities.updateEnableFaceDetectionPreference;

public class CrimeFragment extends Fragment {

    public static final String EXTRA_CRIME_ID = "xtra_crime_id";
    public static final String EXTRA_FACE_DETECTION_ENABLED = "extra_face_detection_enabled";

    private static final String ARG_CRIME_ID = "crime_id";
    private static final String DIALOG_DATE = "DialogDate";
    private static final int REQUEST_DATE = 0;
    private static final int REQUEST_CONTACT = 1;
    private static final int REQUEST_PHOTO = 2;
    private static final int PROFILE_WIDTH = 300;

    private Crime mCrime;
    private File mPhotoFile;
    private EditText mTitleField;
    private Button mDateButton;
    private CheckBox mSolvedCheckbox;
    private Button mReportButton;
    private Button mSuspectButton;
    private Button mGalleryButton;
    private ImageButton mPhotoButton;
    private ImageView mPhotoView;
    private Intent mCaptureImage;
    private Uri mPhotoUri;
    private boolean faceDetectionEnabled;
    private FaceDetector mFaceDetector;

    private class UpdateProfileTask extends AsyncTask<Object, Void, Bitmap> {
        @Override
        protected Bitmap doInBackground(Object... values) {

            Uri uri = Uri.fromFile((File) values[0]);
            Bitmap image = BitmapFactory.decodeFile(uri.getPath());

            Bitmap newImage = scaleDown(image);

            boolean faceDetectionEnabled = (boolean) values[1];

            FaceDetector faceDetector = (FaceDetector) values[2];
            if (faceDetectionEnabled)
                newImage = detectFaces(faceDetector, newImage);

            return scaleDown(newImage);
        }

        @Override
        protected void onPostExecute(Bitmap newImage) {
            mPhotoView.setImageDrawable(null);
            mPhotoView.setImageBitmap(newImage);
        }
    }

    public static CrimeFragment newInstance(UUID crimeId) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_CRIME_ID, crimeId);

        CrimeFragment fragment = new CrimeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UUID crimeId = (UUID) getArguments().getSerializable(ARG_CRIME_ID);
        mCrime = CrimeLab.get(getActivity()).getCrime(crimeId);

        mPhotoFile = CrimeLab.get(getActivity()).getPhotoFile(mCrime);

        mFaceDetector = getFaceDetector(getContext());
    }

    @Override
    public void onPause() {
        super.onPause();

        CrimeLab.get(getActivity())
                .updateCrime(mCrime);
    }

    @Override
    public void onResume() {
        //updatePhotoView();
        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_crime, container, false);

        mTitleField = (EditText) v.findViewById(R.id.crime_title);
        mTitleField.setText(mCrime.getTitle());
        mTitleField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mCrime.setTitle(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mDateButton = (Button) v.findViewById(R.id.crime_date);
        updateDate();
        mDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager manager = getFragmentManager();
                DatePickerFragment dialog = DatePickerFragment
                        .newInstance(mCrime.getDate());
                dialog.setTargetFragment(CrimeFragment.this, REQUEST_DATE);
                dialog.show(manager, DIALOG_DATE);
            }
        });

        mSolvedCheckbox = (CheckBox) v.findViewById(R.id.crime_solved);
        mSolvedCheckbox.setChecked(mCrime.isSolved());
        mSolvedCheckbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mCrime.setSolved(isChecked);
            }
        });

        mReportButton = (Button) v.findViewById(R.id.crime_report);
        mReportButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_TEXT, getCrimeReport());
                i.putExtra(Intent.EXTRA_SUBJECT,
                        getString(R.string.crime_report_subject));
                i = Intent.createChooser(i, getString(R.string.send_report));

                startActivity(i);
            }
        });

        final Intent pickContact = new Intent(Intent.ACTION_PICK,
                ContactsContract.Contacts.CONTENT_URI);
        mSuspectButton = (Button) v.findViewById(R.id.crime_suspect);
        mSuspectButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivityForResult(pickContact, REQUEST_CONTACT);
            }
        });

        if (mCrime.getSuspect() != null) {
            mSuspectButton.setText(mCrime.getSuspect());
        }

        PackageManager packageManager = getActivity().getPackageManager();
        if (packageManager.resolveActivity(pickContact,
                PackageManager.MATCH_DEFAULT_ONLY) == null) {
            mSuspectButton.setEnabled(false);
        }

        mPhotoButton = (ImageButton) v.findViewById(R.id.crime_camera);

        mCaptureImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        mCaptureImage.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        mCaptureImage.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        boolean canTakePhoto = mCaptureImage.resolveActivity(packageManager) != null;
        mPhotoButton.setEnabled(canTakePhoto);

        prepareTakingPhoto();

        mPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(mCaptureImage, REQUEST_PHOTO);
            }
        });
  
        mPhotoView = (ImageView) v.findViewById(R.id.crime_photo);
        mPhotoView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        updatePhotoView();

        mGalleryButton = (Button) v.findViewById(R.id.display_gallery);
        mGalleryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CrimeGalleryActivity.class);
                intent.putExtra(EXTRA_CRIME_ID, mCrime.getId().toString());
                intent.putExtra(EXTRA_FACE_DETECTION_ENABLED, faceDetectionEnabled);
                startActivity(intent);
            }
        });

        CheckBox enableFaceDetectionCheckBox = (CheckBox) v.findViewById(R.id.enable_face_detection_checkbox);
        enableFaceDetectionCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) enableFaceDetection();
                else disableFaceDetection();
                updateEnableFaceDetectionPreference(getContext(), faceDetectionEnabled);
            }
        });

        SharedPreferences preferences = getContext()
                .getSharedPreferences(getString(R.string.crime_preferences_key), Context.MODE_PRIVATE);

        faceDetectionEnabled = preferences.getBoolean(getString(R.string.enable_face_detection_key), false);
        enableFaceDetectionCheckBox.setChecked(faceDetectionEnabled);
        return v;
    }

    private void enableFaceDetection() {
        Log.d("CrimeFragment", "enableFaceDetection");
        faceDetectionEnabled = true;
        updatePhotoView();
    }

    private void disableFaceDetection() {
        Log.d("CrimeFragment", "disableFaceDetection");
        faceDetectionEnabled = false;
        updatePhotoView();
    }

    private void prepareTakingPhoto() {
        File newProfile = Utilities.createNewProfile(getContext(), mCrime);
        if (newProfile != null) {
            mPhotoUri = Uri.fromFile(newProfile);
            Uri externalUri = FileProvider.getUriForFile(getContext(),
                            "com.bignerdranch.android.criminalintent.fileprovider",
                            newProfile);
            mCaptureImage.putExtra(MediaStore.EXTRA_OUTPUT, externalUri);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }

        if (requestCode == REQUEST_DATE) {
            Date date = (Date) data
                    .getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            mCrime.setDate(date);
            updateDate();
        } else if (requestCode == REQUEST_CONTACT && data != null) {
            Uri contactUri = data.getData();
            // Specify which fields you want your query to return
            // values for.
            String[] queryFields = new String[]{
                    ContactsContract.Contacts.DISPLAY_NAME,
            };
            // Perform your query - the contactUri is like a "where"
            // clause here
            ContentResolver resolver = getActivity().getContentResolver();
            Cursor c = resolver
                    .query(contactUri, queryFields, null, null, null);

            try {
                // Double-check that you actually got results
                if (c.getCount() == 0) {
                    return;
                }

                // Pull out the first column of the first row of data -
                // that is your suspect's name.
                c.moveToFirst();

                String suspect = c.getString(0);
                mCrime.setSuspect(suspect);
                mSuspectButton.setText(suspect);
            } finally {
                c.close();
            }
        } else if (requestCode == REQUEST_PHOTO) {
            mPhotoFile = new File(mPhotoUri.getPath());
            updatePhotoView();
            prepareTakingPhoto();
        }
    }

    private void updateDate() {
        mDateButton.setText(mCrime.getDate().toString());
    }

    private String getCrimeReport() {
        String solvedString = null;
        if (mCrime.isSolved()) {
            solvedString = getString(R.string.crime_report_solved);
        } else {
            solvedString = getString(R.string.crime_report_unsolved);
        }
        String dateFormat = "EEE, MMM dd";
        String dateString = DateFormat.format(dateFormat, mCrime.getDate()).toString();
        String suspect = mCrime.getSuspect();
        if (suspect == null) {
            suspect = getString(R.string.crime_report_no_suspect);
        } else {
            suspect = getString(R.string.crime_report_suspect, suspect);
        }
        return getString(R.string.crime_report,
                mCrime.getTitle(), dateString, solvedString, suspect);
    }

    private void updatePhotoView() {

        if (mPhotoFile == null || !mPhotoFile.exists()) {
            mPhotoView.setImageDrawable(null);
        } else {
            new UpdateProfileTask().execute(mPhotoFile, faceDetectionEnabled, mFaceDetector);
        }
    }
}
