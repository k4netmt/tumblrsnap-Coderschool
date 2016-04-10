// Generated code from Butter Knife. Do not modify!
package com.codepath.apps.tumblrsnap.activities;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class PreviewPhotoActivity$$ViewBinder<T extends com.codepath.apps.tumblrsnap.activities.PreviewPhotoActivity> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131099650, "field 'ivPreview'");
    target.ivPreview = finder.castView(view, 2131099650, "field 'ivPreview'");
    view = finder.findRequiredView(source, 2131099651, "field 'rvPhotos'");
    target.rvPhotos = finder.castView(view, 2131099651, "field 'rvPhotos'");
  }

  @Override public void unbind(T target) {
    target.ivPreview = null;
    target.rvPhotos = null;
  }
}
