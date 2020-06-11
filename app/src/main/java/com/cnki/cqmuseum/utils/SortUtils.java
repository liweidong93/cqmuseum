package com.cnki.cqmuseum.utils;

public class SortUtils {

    public void maopaoSort(int arr[]){
        for (int i = arr.length - 1; i > 0; i--){
            for (int j = 0; i < i; j++){
                if (arr[j] > arr[j + 1]){
                    arr[j] = arr[j] + arr[j+1];
                    arr[j+1] = arr[j] - arr[j+1];
                    arr[j] = arr[j] - arr[j+1];
                }
            }
        }
    }
}
