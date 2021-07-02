/*
 * Copyright (c) 2021. Aravind Chowdary (@kamaravichow)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  you may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the License.
 */

package me.aravi.pingit.activities.main.frag;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import me.aravi.pingit.databinding.FragmentRawBinding;


public class RawFragment extends Fragment {

    private static final String PING_RESULT = "ping_result";
    private String pingResult;
    private FragmentRawBinding binding;

    public RawFragment() {
        // Required empty public constructor
    }

    public static ResultFragment newInstance(String pingResult) {
        ResultFragment fragment = new ResultFragment();
        Bundle args = new Bundle();
        args.putString(PING_RESULT, pingResult);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            pingResult = getArguments().getString(PING_RESULT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentRawBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
}