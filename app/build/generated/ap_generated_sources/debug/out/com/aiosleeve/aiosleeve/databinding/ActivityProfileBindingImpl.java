package com.aiosleeve.aiosleeve.databinding;
import com.aiosleeve.aiosleeve.R;
import com.aiosleeve.aiosleeve.BR;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.View;
@SuppressWarnings("unchecked")
public class ActivityProfileBindingImpl extends ActivityProfileBinding  {

    @Nullable
    private static final androidx.databinding.ViewDataBinding.IncludedLayouts sIncludes;
    @Nullable
    private static final android.util.SparseIntArray sViewsWithIds;
    static {
        sIncludes = null;
        sViewsWithIds = new android.util.SparseIntArray();
        sViewsWithIds.put(R.id.fragment_setting_main_layout, 1);
        sViewsWithIds.put(R.id.activity_profile_imageview_back, 2);
        sViewsWithIds.put(R.id.fragment_setting_textview_header, 3);
        sViewsWithIds.put(R.id.activity_profile_scroll_view, 4);
        sViewsWithIds.put(R.id.activity_profile_view_1, 5);
        sViewsWithIds.put(R.id.activity_profile_account_button, 6);
        sViewsWithIds.put(R.id.activity_profile_view_2, 7);
        sViewsWithIds.put(R.id.activity_profile_guides_button, 8);
        sViewsWithIds.put(R.id.activity_profile_view_3, 9);
        sViewsWithIds.put(R.id.activity_profile_privacy_button, 10);
        sViewsWithIds.put(R.id.activity_profile_view_4, 11);
        sViewsWithIds.put(R.id.activity_profile_support_button, 12);
        sViewsWithIds.put(R.id.activity_profile_view_5, 13);
        sViewsWithIds.put(R.id.activity_profile_notification_button, 14);
        sViewsWithIds.put(R.id.activity_profile_notification_status_switch, 15);
        sViewsWithIds.put(R.id.activity_profile_view_6, 16);
        sViewsWithIds.put(R.id.activity_profile_logout_button, 17);
        sViewsWithIds.put(R.id.activity_profile_view_7, 18);
        sViewsWithIds.put(R.id.activity_profile_version, 19);
    }
    // views
    // variables
    // values
    // listeners
    // Inverse Binding Event Handlers

    public ActivityProfileBindingImpl(@Nullable androidx.databinding.DataBindingComponent bindingComponent, @NonNull View root) {
        this(bindingComponent, root, mapBindings(bindingComponent, root, 20, sIncludes, sViewsWithIds));
    }
    private ActivityProfileBindingImpl(androidx.databinding.DataBindingComponent bindingComponent, View root, Object[] bindings) {
        super(bindingComponent, root, 0
            , (android.widget.TextView) bindings[6]
            , (android.widget.TextView) bindings[8]
            , (android.widget.ImageView) bindings[2]
            , (android.widget.TextView) bindings[17]
            , (android.widget.TextView) bindings[14]
            , (androidx.appcompat.widget.SwitchCompat) bindings[15]
            , (android.widget.TextView) bindings[10]
            , (android.widget.ScrollView) bindings[4]
            , (android.widget.TextView) bindings[12]
            , (android.widget.TextView) bindings[19]
            , (android.view.View) bindings[5]
            , (android.view.View) bindings[7]
            , (android.view.View) bindings[9]
            , (android.view.View) bindings[11]
            , (android.view.View) bindings[13]
            , (android.view.View) bindings[16]
            , (android.view.View) bindings[18]
            , (android.widget.RelativeLayout) bindings[0]
            , (android.widget.RelativeLayout) bindings[1]
            , (android.widget.TextView) bindings[3]
            );
        this.activitySignupMainRealtive.setTag(null);
        setRootTag(root);
        // listeners
        invalidateAll();
    }

    @Override
    public void invalidateAll() {
        synchronized(this) {
                mDirtyFlags = 0x1L;
        }
        requestRebind();
    }

    @Override
    public boolean hasPendingBindings() {
        synchronized(this) {
            if (mDirtyFlags != 0) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean setVariable(int variableId, @Nullable Object variable)  {
        boolean variableSet = true;
            return variableSet;
    }

    @Override
    protected boolean onFieldChange(int localFieldId, Object object, int fieldId) {
        switch (localFieldId) {
        }
        return false;
    }

    @Override
    protected void executeBindings() {
        long dirtyFlags = 0;
        synchronized(this) {
            dirtyFlags = mDirtyFlags;
            mDirtyFlags = 0;
        }
        // batch finished
    }
    // Listener Stub Implementations
    // callback impls
    // dirty flag
    private  long mDirtyFlags = 0xffffffffffffffffL;
    /* flag mapping
        flag 0 (0x1L): null
    flag mapping end*/
    //end
}