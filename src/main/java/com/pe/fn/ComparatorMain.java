package com.pe.fn;


import com.pe.fn.util.Comparator;

import java.io.File;
import java.util.Scanner;

/**
 *This {@code ComparatorMain} is a main class that invokes using arguments
 * of length having file path to compare HTTP request outputs.
 *
 * The outPut of each call is in Json & that can be Inner Json as well.
 *
 */
public class ComparatorMain {

    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
            System.out.println("The both file path should be provided as 'filePath1' 'filePath2' in command line arguments");
            System.exit(1);
        }

        Comparator comparator = new Comparator();
        Wrapper wrapper = comparator.getData(new File(args[0]), new File(args[1]));
        System.out.println("The events is fully matched : " + comparator.compare((Scanner) wrapper.getX(), (Scanner) wrapper.getY()));
    }
}
