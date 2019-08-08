
This is the Utility to compare to Json objects and taken care of following things:

-->Create a comparator library that can be used to compare 2 API responses (HTTP/HTTPS)
   The input to your code should be two files with millions of request URLs.
   
   File1 File2
   https://reqres.in/api/users/3 https://reqres.in/api/users/2
   https://reqres.in/api/users/1 https://reqres.in/api/users?page=3
   https://reqres.in/api/users/2 /api/unknown/2
   https://reqres.in/api/users?page=2 https://reqres.in/api/users?page=2
   https://reqres.in/api/users?page=1 … and so on
   …. And so on
   Your code should read the (HTTP/HTTPS) requests from File 1 and File 2, use any http client library, and
   compare the response of
   File 1 : line 1 & File 2 : line 1,
   File 1 : line 2 & File 2 : line 2,
   File 1 : line 3 & File 2 : line 3,
   ....and so on
   
/**
 *This {@code ComparatorMain} is a main class that invokes using arguments
 * of length having file path to compare HTTP request outputs.
 *
 * The outPut of each call is in Json & that can be Inner Json as well.
 *
 */
   
How to run the main block:
    1. ComparatorMain is a main class.
    2. It requires two command line arguments as file path 1 and file path 2. 
    3. File paths sample are in /resources/file1.txt|file2.txt 