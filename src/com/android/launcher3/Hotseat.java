/*
 * Copyright (C) 2011 The Android Open Source Project
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

package com.android.launcher3;

import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;

public class Hotseat extends PagedView
        implements Stats.LaunchSourceProvider {

    private final boolean mHasVerticalHotseat;
    private CellLayout mContent;
    private Launcher mLauncher;
    private int mAllAppsButtonRank;

    public Hotseat(Context context) {
        this(context, null);
    }

    public Hotseat(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public Hotseat(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        mFadeInAdjacentScreens = false;
        //mHandleScrollIndicator = true;

        setDataIsReady();

        int hotseatPages = 3;

        LayoutInflater inflater =
                (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        for (int i = 0; i < hotseatPages; i++) {
            inflater.inflate(R.layout.hotseat_page, this);
        }
        setCurrentPage(2);

        mAllowOverScroll = true;
        mLauncher = (Launcher) context;
        mHasVerticalHotseat = mLauncher.getDeviceProfile().isVerticalBarLayout();
    }

    CellLayout getLayout() {
        return (CellLayout) getPageAt(mCurrentPage);
    }

    public boolean hasPage(View view) {
        for (int i = 0; i < getChildCount(); i++) {
            if (view == getChildAt(i)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns whether there are other icons than the all apps button in the hotseat.
     */
    public boolean hasIcons() {
        return mContent.getShortcutsAndWidgets().getChildCount() > 1;
    }

    /**
     * Registers the specified listener on the cell layout of the hotseat.
     */
    @Override
    public void setOnLongClickListener(OnLongClickListener l) {
        for (int i = 0; i < getChildCount(); i++) {
            getPageAt(i).setOnLongClickListener(l);
        }
    }

    /* Get the orientation invariant order of the item in the hotseat for persistence. */
    int getOrderInHotseat(int x, int y) {
        return mHasVerticalHotseat ? (mCellCountY - y - 1) : x;
    }

    /* Get the orientation specific coordinates given an invariant order in the hotseat. */
    int getCellXFromOrder(int rank) {
        return mHasVerticalHotseat ? 0 : rank;
    }

    int getCellYFromOrder(int rank) {
        return mHasVerticalHotseat ? (mCellCountY - (rank + 1)) : 0;
    }

    public boolean isAllAppsButtonRank(int rank) {
        return rank == mAllAppsButtonRank;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        updateHotseat();
    }

    public void updateHotseat() {
        DeviceProfile grid = mLauncher.getDeviceProfile();

        mAllAppsButtonRank = grid.inv.hotseatAllAppsRank;
        if (grid.isLandscape && !grid.isLargeTablet) {
            mCellCountX = 1;
            mCellCountY = (int) grid.inv.numHotseatIcons;
        } else {
            mCellCountX = (int) grid.inv.numHotseatIcons;
            mCellCountY = 1;
        }
        for (int i = 0; i < getChildCount(); i++) {
            Log.d("TEST", "item=" + i);
            CellLayout cl = (CellLayout) getPageAt(i);
            cl.setGridSize(mCellCountX, mCellCountY);
            cl.setIsHotseat(true);
            cl.updateHotseatScale(grid);
        }
        resetLayout();
    }

    void resetLayout() {
        for (int i = 0; i < getChildCount(); i++) {
            ((CellLayout) getPageAt(i)).removeAllViewsInLayout();
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        // We don't want any clicks to go through to the hotseat unless the workspace is in
        // the normal state.
        return mLauncher.getWorkspace().workspaceInModalState();
    }

    @Override
    public void fillInLaunchSourceData(Bundle sourceData) {
        sourceData.putString(Stats.SOURCE_EXTRA_CONTAINER, Stats.CONTAINER_HOTSEAT);
    }

    @Override
    protected void getEdgeVerticalPostion(int[] pos) {
        View child = getChildAt(getPageCount() - 1);
        pos[0] = child.getTop();
        pos[1] = child.getBottom();
    }

    @Override
    public void syncPages() {
    }

    @Override
    public void syncPageItems(int page, boolean immediate) {
    }
}
