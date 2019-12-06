/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.car.settings.common;

import android.car.drivingstate.CarUxRestrictions;
import android.car.drivingstate.CarUxRestrictionsManager;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;

import com.android.car.settings.R;
import com.android.car.ui.toolbar.MenuItem;
import com.android.car.ui.toolbar.Toolbar;

import java.util.List;

/**
 * Base fragment for setting activity.
 */
public abstract class BaseFragment extends Fragment implements
        CarUxRestrictionsManager.OnUxRestrictionsChangedListener {

    /**
     * Assume The activity holds this fragment also implements the FragmentController.
     * This function should be called after onAttach()
     */
    public final FragmentController getFragmentController() {
        return (FragmentController) getActivity();
    }

    /**
     * Assume The activity holds this fragment also implements the UxRestrictionsProvider.
     * This function should be called after onAttach()
     */
    protected final CarUxRestrictions getCurrentRestrictions() {
        return ((UxRestrictionsProvider) getActivity()).getCarUxRestrictions();
    }

    /**
     * Checks if this fragment can be shown or not given the CarUxRestrictions. Default to
     * {@code false} if UX_RESTRICTIONS_NO_SETUP is set.
     */
    protected boolean canBeShown(@NonNull CarUxRestrictions carUxRestrictions) {
        return !CarUxRestrictionsHelper.isNoSetup(carUxRestrictions);
    }

    @Override
    public void onUxRestrictionsChanged(CarUxRestrictions restrictionInfo) {
    }

    /**
     * Returns the layout id of the current Fragment.
     */
    @LayoutRes
    protected abstract int getLayoutId();

    /**
     * Returns the string id for the current Fragment title. Subclasses should override this
     * method to set the title to display. Use {@link #setTitle(CharSequence)} to update the
     * displayed title while resumed. The default title is the Settings Activity label.
     */
    @StringRes
    protected int getTitleId() {
        return R.string.settings_label;
    }

    /**
     * Returns the MenuItems to display in the toolbar. Subclasses should override this to
     * add additional buttons, switches, ect. to the toolbar.
     */
    protected List<MenuItem> getToolbarMenuItems() {
        return null;
    }

    protected Toolbar.State getToolbarState() {
        return Toolbar.State.SUBPAGE;
    }

    protected Toolbar.NavButtonMode getToolbarNavButtonStyle() {
        return Toolbar.NavButtonMode.BACK;
    }

    protected final Toolbar getToolbar() {
        return requireActivity().findViewById(R.id.toolbar);
    }

    /**
     * Should be used to override fragment's title. This should only be called after
     * {@link #onActivityCreated(Bundle)}.
     *
     * @param title CharSequence to set as the new title.
     */
    protected final void setTitle(CharSequence title) {
        TextView titleView = requireActivity().findViewById(R.id.title);
        if (titleView != null) {
            titleView.setText(title);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (!(getActivity() instanceof FragmentController)) {
            throw new IllegalStateException("Must attach to a FragmentController");
        }
        if (!(getActivity() instanceof UxRestrictionsProvider)) {
            throw new IllegalStateException("Must attach to a UxRestrictionsProvider");
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        @LayoutRes int layoutId = getLayoutId();
        return inflater.inflate(layoutId, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Toolbar toolbar = getToolbar();
        if (toolbar != null) {
            List<MenuItem> items = getToolbarMenuItems();
            if (items != null) {
                if (items.size() == 1) {
                    items.get(0).setId(R.id.toolbar_menu_item_0);
                } else if (items.size() == 2) {
                    items.get(0).setId(R.id.toolbar_menu_item_0);
                    items.get(1).setId(R.id.toolbar_menu_item_1);
                }
            }
            toolbar.setTitle(getTitleId());
            toolbar.setMenuItems(items);
            toolbar.setState(getToolbarState());
            toolbar.setNavButtonMode(getToolbarNavButtonStyle());
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        onUxRestrictionsChanged(getCurrentRestrictions());
    }

    /**
     * Allow fragment to intercept back press and customize behavior.
     */
    protected void onBackPressed() {
        getFragmentController().goBack();
    }
}
