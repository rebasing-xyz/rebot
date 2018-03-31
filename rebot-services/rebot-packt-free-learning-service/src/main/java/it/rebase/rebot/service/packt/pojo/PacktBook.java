/*
  The MIT License (MIT)

  Copyright (c) 2017 Rebase.it ReBot <just@rebase.it>

  Permission is hereby granted, free of charge, to any person obtaining a copy of
  this software and associated documentation files (the "Software"), to deal in
  the Software without restriction, including without limitation the rights to
  use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
  the Software, and to permit persons to whom the Software is furnished to do so,
  subject to the following conditions:

  The above copyright notice and this permission notice shall be included in all
  copies or substantial portions of the Software.

  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
  FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
  COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
  IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
  CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package it.rebase.rebot.service.packt.pojo;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class PacktBook {

    private String bookName;
    private String claimUrl;

    public String getBookName() {
        String[] bookName = this.bookName.replace("-", " ").split(" ");
        String tempBookName = "";
        for (String name : bookName) {
            if (!name.equals(bookName[bookName.length - 1])) {
                tempBookName += name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase() + " ";
            } else {
                tempBookName += name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();
            }
        }
        System.out.println(tempBookName);
        return tempBookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public String getClaimUrl() {
        return claimUrl;
    }

    public void setClaimUrl(String claimUrl) {
        this.claimUrl = claimUrl;
    }

    @Override
    public String toString() {
        return "PacktBook - {" +
                "bookName='" + bookName + '\'' +
                ", claimUrl='" + claimUrl + '\'' +
                '}';
    }

}
