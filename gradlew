<?xml version="1.0" encoding="utf-8"?>
<layout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"

    tools:context=".ui.show_details.ShowDetailsFragment">

    <data>

        <import type="android.view.View" />

        <variable
            name="viewmodel"
            type="com.example.tvshows.ui.show_details.ShowDetailsViewModel" />

        <variable
            name="view"
            type="android.view.View" />
    </data>

    <ScrollView
        android:id="@+id/main_scrollview"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:scrollbarThumbVertical="@drawable/scrollbar_style">

        <LinearLayout
            android:id="@+id/linear"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:orientation="vertical">

            <RelativeLayout
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:layout_marginEnd="8dp"
                android:clickable="true"
                android:focusable="true"
                android:foreground="?android:attr/selectableItemBackground">


                <com.google.android.material.card.MaterialCardView
                    android:id="@+id/secCard"
                    android:layout_width="133dp"
                    android:layout_height="178dp"
                    android:layout_marginStart="16dp"
                    android:layout_marginTop="28dp"
                    android:backgroundTint="#00000000"
                    android:paddingStart="2dp"
                    android:paddingTop="8dp"
                    android:paddingEnd="2dp"
                    android:paddingBottom="8dp"
                    app:cardCornerRadius="15dp"
                    app:cardElevation="0dp"
                    app:layout_constraintStart_toStartOf="parent"
                    app:layout_constraintTop_toTopOf="parent">

                    <ProgressBar
                        android:id="@+id/progressBar_"
                        android:layout_width="172dp"
                        android:layout_height="79dp"
                        android:layout_centerVertical="true"
                        android:layout_gravity="center"
                        android:indeterminateTint="@color/colorPrimary" />

                    <ImageView
                        android:id="@+id/imageView2"
                        android:layout_width="match_parent"
                        android:layout_height="179dp"
                        android:contentDescription="@string/image"
                        android:scaleType="fitXY"

                        app:imageDetails="@{viewmodel.details.poster_path}"
                        app:progressBar_details="@{progressBar}" />

                </com.google.android.material.card.MaterialCardView>


                <com.google.android.material.floatingactionbutton.FloatingActionButton
                    android:id="@+id/pin_icon"
                    android:layout_width="wrap_content"
                    android:layout_height="wrap_content"
                    android:layout_marginStart="8dp"
                    android:layout_marginTop="8dp"
                    android:backgroundTint="@color/colorPrimaryTransparent"
                    android:scaleType="center"
                    android:src="@drawable/pin"

                    app:fabSize="normal"
                    app:layout_constraintStart_toStartOf="parent"
                    app:layout_constraintTop_toTopOf="parent"
                    app:tint="@color/textColorPrimary" />

                <RelativeLayout
                    android:layout_width="match_parent"
                    android:layout_height="wrap_content"
                    android:layout_toEndOf="@+id/secCard">

                    <TextView
                        android:id="@+id/title_details"
                        android:layout_width="match_parent"
                        android:layout_height="wrap_content"
                        android:layout_marginStart="13dp"
                        android:layout_marginTop="28dp"
                        android:background="@drawable/circled_bordered_smaller_radius"
                        android:gravity="center"
                        android:padding="3dp"
                        android:text="@string/episodes_details"
                        android:textColor="@color/textColorPrimary"
                        android:textSize="20sp"
                        android:textStyle="bold"
                        app:layout_constraintEnd_toEndOf="parent"
                        app:layout_constraintHorizontal_bias="0.619"
                        app:layout_constraintTop_toTopOf="parent" />

                    <GridLayout
                        android:layout_width="match_parent"
                        android:layout_height="wrap_content"
                        android:layout_below="@+id/title_details"
                        android:layout_marginStart="13dp