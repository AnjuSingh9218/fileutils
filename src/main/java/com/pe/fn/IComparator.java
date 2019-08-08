package com.pe.fn;

import java.io.File;
import java.io.FileNotFoundException;

public interface IComparator <X, Y> {
    boolean compare(X x, Y y);
    Wrapper<X, Y> getData(File file1, File file2) throws FileNotFoundException;
}
