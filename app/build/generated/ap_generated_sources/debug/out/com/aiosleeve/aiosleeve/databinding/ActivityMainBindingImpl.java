package com.aiosleeve.aiosleeve.databinding;
import com.aiosleeve.aiosleeve.R;
import com.aiosleeve.aiosleeve.BR;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.View;
@SuppressWarnings("unchecked")
public class ActivityMainBindingImpl extends ActivityMainBinding  {

    @Nullable
    private static final androidx.databinding.ViewDataBinding.IncludedLayouts sIncludes;
    @Nullable
    private static final android.util.SparseIntArray sViewsWithIds;
    static {
        sIncludes = null;
        sViewsWithIds = new android.util.SparseIntArray();
        sViewsWithIds.put(R.id.activity_main_coordinator_layout, 1);
        sViewsWithIds.put(R.id.activity_main_appBarLayout, 2);
        sViewsWithIds.put(R.id.activity_main_toolbar, 3);
        sViewsWithIds.put(R.id.bottomNavigationView, 4);
        sViewsWithIds.put(R.id.activity_main_view_pager, 5);
        sViewsWithIds.put(R.id.activity_main_navigation_view, 6);
    }
    // views
    // variables
    // values
    // listeners
    // Inverse Binding Event Handlers

    public ActivityMainBindingImpl(@Nullable androidx.databinding.DataBindingComponent bindingComponent, @NonNull View root) {
        this(bindingComponent, root, mapBindings(bindingComponent, root, 7, sIncludes, sViewsWithIds));
    }
    private ActivityMainBindingImpl(androidx.databinding.DataBindingComponent bindingComponent, View root, Object[] bindings) {
        super(bindingComponent, root, 0
            , (com.google.android.material.appbar.AppBarLayout) bindings[2]
            , (androidx.constraintlayout.widget.ConstraintLayout) bindings[1]
            , (androidx.drawerlayout.widget.DrawerLayout) bindings[0]
            , (com.google.android.material.navigation.NavigationView) bindings[6]
            , (androidx.appcompat.widget.Toolbar) bindings[3]
            , (com.aiosleeve.aiosleeve.view.NonSwipeableViewPager) bindings[5]
            , (com.roughike.bottombar.BottomBar) bindings[4]
            );
        this.activityMainDrawerLayout.setTag(null);
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