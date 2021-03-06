/*
 * Entagged Audio Tag library
 * Copyright (c) 2003-2005 Rapha�l Slinckx <raphael@slinckx.net>
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *  
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */
package com.hadeslee.audiotag.audio.ogg;

import com.hadeslee.audiotag.audio.exceptions.CannotReadException;
import com.hadeslee.audiotag.audio.generic.AudioFileReader;
import com.hadeslee.audiotag.audio.generic.GenericAudioHeader;
import com.hadeslee.audiotag.audio.ogg.util.OggInfoReader;
import com.hadeslee.audiotag.audio.ogg.util.OggPageHeader;
import com.hadeslee.audiotag.tag.Tag;

import java.io.*;
import java.util.logging.Logger;

/**
 * Read Ogg File Tag and Encoding information
 * <p/>
 * Only implemented for ogg files containing a vorbis stream with vorbis comments
 */
public class OggFileReader extends AudioFileReader
{
    // Logger Object
    public static Logger logger = Logger.getLogger("com.hadeslee.jaudiotagger.audio.ogg");

    private OggInfoReader ir = new OggInfoReader();
    private OggVorbisTagReader vtr = new OggVorbisTagReader();

    protected GenericAudioHeader getEncodingInfo(RandomAccessFile raf) throws CannotReadException, IOException
    {
        return ir.read(raf);
    }

    protected Tag getTag(RandomAccessFile raf) throws CannotReadException, IOException
    {
        return vtr.read(raf);
    }

     /**
     * Return count Ogg Page header, count starts from zero
     *
     * count=0; should return PageHeader that contains Vorbis Identification Header
     * count=1; should return Pageheader that contains VorbisComment and possibly SetupHeader
     * count>=2; should return PageHeader containng remaining VorbisComment,SetupHeader and/or Audio
     *
     * @param raf
     * @param count
     * @return
     * @throws CannotReadException
     * @throws IOException
     */
    public OggPageHeader readOggPageHeader(RandomAccessFile raf,int count) throws CannotReadException, IOException
    {
        OggPageHeader pageHeader = OggPageHeader.read (raf);
        while(count >0)
        {
            raf.seek(raf.getFilePointer() + pageHeader.getPageLength());
            pageHeader = OggPageHeader.read (raf);
            count--;
        }
        return pageHeader;
    }

    /**
     * Summarize all the ogg headers in a file
     *
     * A useful utility function
     *
     * @param oggFile
     * @throws CannotReadException
     * @throws IOException
     */
    public void summarizeOggPageHeaders(File oggFile)throws CannotReadException, IOException
    {
        RandomAccessFile raf = new RandomAccessFile(oggFile,"r");

        while(raf.getFilePointer()<raf.length())
        {
            System.out.println("pageHeader starts at:"+raf.getFilePointer());
            OggPageHeader pageHeader = OggPageHeader.read (raf);
            System.out.println("pageHeader finishes at:"+raf.getFilePointer());
            System.out.println(pageHeader+"\n");
            raf.seek(raf.getFilePointer() + pageHeader.getPageLength());
        }
        System.out.println("Raf File Pointer at:"+raf.getFilePointer() +"File Size is:"+raf.length());
    }
}

