package org.thera_pi.nebraska.gui.utils;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Properties;
import java.util.Set;
import java.util.Map.Entry;


public final class INIFile
{

    private String mstrDateFmt = "dd.mm.yyyy";


    private String mstrTimeStampFmt = "dd.mm.yyyy hh:mm:ss";


    private boolean mblnLoaded = false;


    private String mstrFile;


    private LinkedHashMap<String, INISection> mhmapSections;

 
    private Properties mpropEnv;

    public INIFile(String pstrPathAndName)
    {
        this.mpropEnv = getEnvVars();
        this.mhmapSections = new LinkedHashMap<String, INISection>();
        this.mstrFile = pstrPathAndName;
        if (checkFile(pstrPathAndName)) loadFile();
    }


    public String getFileName()
    {
        return this.mstrFile;
    }
    public String getStringProperty(String pstrSection, String pstrProp)
    {
        String      strRet   = null;
        INIProperty objProp  = null;
        INISection  objSec   = null;

        objSec = (INISection) this.mhmapSections.get(pstrSection);
        if (objSec != null)
        {
            objProp = objSec.getProperty(pstrProp);
            if (objProp != null)
            {
                strRet = objProp.getPropValue();
                objProp = null;
            }
            objSec = null;
        }
        return strRet;
    }


    public Boolean getBooleanProperty(String pstrSection, String pstrProp)
    {
        boolean     blnRet  = false;
        String      strVal  = null;
        INIProperty objProp = null;
        INISection  objSec  = null;

        objSec = (INISection) this.mhmapSections.get(pstrSection);
        if (objSec != null)
        {
            objProp = objSec.getProperty(pstrProp);
            if (objProp != null)
            {
                strVal = objProp.getPropValue().toUpperCase();
                if (strVal.equals("YES") || strVal.equals("TRUE") ||
                    strVal.equals("1"))
                {
                    blnRet = true;
                }
                objProp = null;
            }
            objSec = null;
        }
        return new Boolean(blnRet);
    }

  
    public Integer getIntegerProperty(String pstrSection, String pstrProp)
    {
        Integer     intRet  = null;
        String      strVal  = null;
        INIProperty objProp = null;
        INISection  objSec  = null;

        objSec = (INISection) this.mhmapSections.get(pstrSection);
        if (objSec != null)
        {
            objProp = objSec.getProperty(pstrProp);
            try
            {
                if (objProp != null)
                {
                    strVal = objProp.getPropValue();
                    if (strVal != null) intRet = new Integer(strVal);
                }
            }
            catch (NumberFormatException NFExIgnore)
            {
            }
            finally
            {
                if (objProp != null) objProp = null;
            }
            objSec = null;
        }
        return intRet;
    }

 
    public Long getLongProperty(String pstrSection, String pstrProp)
    {
        Long        lngRet  = null;
        String      strVal  = null;
        INIProperty objProp = null;
        INISection  objSec  = null;

        objSec = (INISection) this.mhmapSections.get(pstrSection);
        if (objSec != null)
        {
            objProp = objSec.getProperty(pstrProp);
            try
            {
                if (objProp != null)
                {
                    strVal = objProp.getPropValue();
                    if (strVal != null) lngRet = new Long(strVal);
                }
            }
            catch (NumberFormatException NFExIgnore)
            {
            }
            finally
            {
                if (objProp != null) objProp = null;
            }
            objSec = null;
        }
        return lngRet;
    }

    public Double getDoubleProperty(String pstrSection, String pstrProp)
    {
        Double      dblRet  = null;
        String      strVal  = null;
        INIProperty objProp = null;
        INISection  objSec  = null;

        objSec = (INISection) this.mhmapSections.get(pstrSection);
        if (objSec != null)
        {
            objProp = objSec.getProperty(pstrProp);
            try
            {
                if (objProp != null)
                {
                    strVal = objProp.getPropValue();
                    if (strVal != null) dblRet = new Double(strVal);
                }
            }
            catch (NumberFormatException NFExIgnore)
            {
            }
            finally
            {
                if (objProp != null) objProp = null;
            }
            objSec = null;
        }
        return dblRet;
    }


    public Date getDateProperty(String pstrSection, String pstrProp)
    {
        Date        dtRet   = null;
        String      strVal  = null;
        DateFormat  dtFmt   = null;
        INIProperty objProp = null;
        INISection  objSec  = null;

        objSec = (INISection) this.mhmapSections.get(pstrSection);
        if (objSec != null)
        {
            objProp = objSec.getProperty(pstrProp);
            try
            {
                if (objProp != null) strVal = objProp.getPropValue();
                if (strVal != null)
                {
                    dtFmt = new SimpleDateFormat(this.mstrDateFmt);
                    dtRet = dtFmt.parse(strVal);
                }
            }
            catch (ParseException PExIgnore)
            {
            }
            catch (IllegalArgumentException IAEx)
            {
            }
            finally
            {
                if (objProp != null) objProp = null;
            }
            objSec = null;
        }
        return dtRet;
    }

 
    public Date getTimestampProperty(String pstrSection, String pstrProp)
    {
        Timestamp   tsRet   = null;
        Date        dtTmp   = null;
        String      strVal  = null;
        DateFormat  dtFmt   = null;
        INIProperty objProp = null;
        INISection  objSec  = null;

        objSec = (INISection) this.mhmapSections.get(pstrSection);
        if (objSec != null)
        {
            objProp = objSec.getProperty(pstrProp);
            try
            {
                if (objProp != null) strVal = objProp.getPropValue();
                if (strVal != null)
                {
                    dtFmt = new SimpleDateFormat(this.mstrDateFmt);
                    dtTmp = dtFmt.parse(strVal);
                    tsRet = new Timestamp(dtTmp.getTime());
                }
            }
            catch (ParseException PExIgnore)
            {
            }
            catch (IllegalArgumentException IAEx)
            {
            }
            finally
            {
                if (objProp != null) objProp = null;
            }
            objSec = null;
        }
        return tsRet;
    }

    public void addSection(String pstrSection, String pstrComments)
    {
        INISection objSec   = null;

        objSec = (INISection) this.mhmapSections.get(pstrSection);
        if (objSec == null)
        {
            objSec = new INISection(pstrSection);
            this.mhmapSections.put(pstrSection, objSec);
        }
        objSec.setSecComments(delRemChars(pstrComments));
        objSec = null;
    }

 
    public void setStringProperty(String pstrSection, String pstrProp, 
                    				String pstrVal, String pstrComments)
    {
        INISection objSec   = null;

        objSec = (INISection) this.mhmapSections.get(pstrSection);
        if (objSec == null)
        {
            objSec = new INISection(pstrSection);
            this.mhmapSections.put(pstrSection, objSec);
        }
        objSec.setProperty(pstrProp, pstrVal, pstrComments);
    }


    public void setBooleanProperty(String pstrSection, String pstrProp, 
                    				boolean pblnVal, String pstrComments)
    {
        INISection objSec   = null;

        objSec = (INISection) this.mhmapSections.get(pstrSection);
        if (objSec == null)
        {
            objSec = new INISection(pstrSection);
            this.mhmapSections.put(pstrSection, objSec);
        }
        if (pblnVal)
            objSec.setProperty(pstrProp, "TRUE", pstrComments);
        else
            objSec.setProperty(pstrProp, "FALSE", pstrComments);
    }

 
    public void setIntegerProperty(String pstrSection, String pstrProp, 
                    				int pintVal, String pstrComments)
    {
        INISection objSec   = null;

        objSec = (INISection) this.mhmapSections.get(pstrSection);
        if (objSec == null)
        {
            objSec = new INISection(pstrSection);
            this.mhmapSections.put(pstrSection, objSec);
        }
        objSec.setProperty(pstrProp, Integer.toString(pintVal), pstrComments);
    }


    public void setLongProperty(String pstrSection, String pstrProp, 
                    			long plngVal, String pstrComments)
    {
        INISection objSec   = null;

        objSec = (INISection) this.mhmapSections.get(pstrSection);
        if (objSec == null)
        {
            objSec = new INISection(pstrSection);
            this.mhmapSections.put(pstrSection, objSec);
        }
        objSec.setProperty(pstrProp, Long.toString(plngVal), pstrComments);
    }


    public void setDoubleProperty(String pstrSection, String pstrProp, 
                    				double pdblVal, String pstrComments)
    {
        INISection objSec   = null;

        objSec = (INISection) this.mhmapSections.get(pstrSection);
        if (objSec == null)
        {
            objSec = new INISection(pstrSection);
            this.mhmapSections.put(pstrSection, objSec);
        }
        objSec.setProperty(pstrProp, Double.toString(pdblVal), pstrComments);
    }


    public void setDateProperty(String pstrSection, String pstrProp, 
                    			Date pdtVal, String pstrComments)
    {
        INISection objSec   = null;

        objSec = (INISection) this.mhmapSections.get(pstrSection);
        if (objSec == null)
        {
            objSec = new INISection(pstrSection);
            this.mhmapSections.put(pstrSection, objSec);
        }
        objSec.setProperty(pstrProp, utilDateToStr(pdtVal, this.mstrDateFmt), 
                        	pstrComments);
    }


    public void setTimestampProperty(String pstrSection, String pstrProp, 
                    					Timestamp ptsVal, String pstrComments)
    {
        INISection objSec   = null;

        objSec = (INISection) this.mhmapSections.get(pstrSection);
        if (objSec == null)
        {
            objSec = new INISection(pstrSection);
            this.mhmapSections.put(pstrSection, objSec);
        }
        objSec.setProperty(pstrProp, timeToStr(ptsVal, this.mstrTimeStampFmt), 
                        	pstrComments);
    }

 
    public void setDateFormat(String pstrDtFmt) throws IllegalArgumentException
    {
        if (!checkDateTimeFormat(pstrDtFmt))
            throw new IllegalArgumentException("The specified date pattern is invalid!");
        this.mstrDateFmt = pstrDtFmt;
    }

  
    public void setTimeStampFormat(String pstrTSFmt)
    {
        if (!checkDateTimeFormat(pstrTSFmt))
            throw new IllegalArgumentException("The specified timestamp pattern is invalid!");
        this.mstrTimeStampFmt = pstrTSFmt;
    }


    public int getTotalSections()
    {
        return this.mhmapSections.size();
    }


    public String[] getAllSectionNames()
    {
        int        iCntr  = 0;
        Iterator<String>   iter   = null;
        String[]   arrRet = null;

        try
        {
            if (this.mhmapSections.size() > 0)
            {
                arrRet = new String[this.mhmapSections.size()];
                for (iter = this.mhmapSections.keySet().iterator();;iter.hasNext())
                {
                    arrRet[iCntr] = (String) iter.next();
                    iCntr++;
                }
            }
        }
        catch (NoSuchElementException NSEExIgnore)
        {
        }
        finally
        {
            if (iter != null) iter = null;
        }
        return arrRet;
    }


    public String[] getPropertyNames(String pstrSection)
    {
        String[]   arrRet = null;
        INISection objSec = null;

        objSec = (INISection) this.mhmapSections.get(pstrSection);
        if (objSec != null)
        {
            arrRet = objSec.getPropNames();
            objSec = null;
        }
        return arrRet;
    }


    public Map<String, INIProperty> getProperties(String pstrSection)
    {
        Map<String, INIProperty>        hmRet = null;
        INISection objSec = null;

        objSec = (INISection) this.mhmapSections.get(pstrSection);
        if (objSec != null)
        {
            hmRet = objSec.getProperties();
            objSec = null;
        }
        return hmRet;
    }


    public void removeProperty(String pstrSection, String pstrProp)
    {
        INISection objSec = null;

        objSec = (INISection) this.mhmapSections.get(pstrSection);
        if (objSec != null)
        {
            objSec.removeProperty(pstrProp);
        	objSec = null;
        }
    }

 
    public void removeSection(String pstrSection)
    {
        if (this.mhmapSections.containsKey(pstrSection))
            this.mhmapSections.remove(pstrSection);
    }

 
    public boolean save()
    {
        boolean    blnRet    = false;
        File       objFile   = null;
        String     strName   = null;
        String     strTemp   = null;
        Iterator<String>   itrSec    = null;
        INISection objSec    = null;
        FileWriter objWriter = null;

        try
        {
            if (this.mhmapSections.size() == 0) return false;
            objFile = new File(this.mstrFile);
            if (objFile.exists()) objFile.delete();
            objWriter = new FileWriter(objFile);
            itrSec = this.mhmapSections.keySet().iterator();
            while (itrSec.hasNext())
            {
                strName = (String) itrSec.next();
                objSec = (INISection) this.mhmapSections.get(strName);
                strTemp = objSec.toString();
                objWriter.write(strTemp);
                objWriter.write("\r\n");
                objSec = null;
            }
            blnRet = true;
        }
        catch (IOException IOExIgnore)
        {
        }
        finally
        {
            if (objWriter != null)
            {
                closeWriter(objWriter);
                objWriter = null;
            }
            if (objFile != null) objFile = null;
            if (itrSec != null) itrSec = null;
        }
        return blnRet;
    }


    private Properties getEnvVars()
    {
    	Map<String, String> env = System.getenv();
        Properties envVars = new Properties();
        for(Entry<String,String> entry: env.entrySet())
        {
        	envVars.setProperty(entry.getKey(), entry.getValue());
        }

        return envVars;
    }

 
    private boolean checkDateTimeFormat(String pstrDtFmt)
    {
        boolean    blnRet = false;
        DateFormat objFmt = null;

        try
        {
            objFmt = new SimpleDateFormat(pstrDtFmt);
            blnRet = true;
        }
        catch (NullPointerException NPExIgnore)
        {
        }
        catch (IllegalArgumentException IAExIgnore)
        {
        }
        finally
        {
            if (objFmt != null) objFmt = null;
        }
        return blnRet;
    }

 
    private void loadFile()
    {
        int            iPos       = -1;
        String         strLine    = null;
        String         strSection = null;
        String         strRemarks = null;
        BufferedReader objBRdr    = null;
        FileReader     objFRdr    = null;
        INISection     objSec     = null;

        try
        {
            objFRdr = new FileReader(this.mstrFile);
            if (objFRdr != null)
            {
                objBRdr = new BufferedReader(objFRdr);
                if (objBRdr != null)
                {
                    while (objBRdr.ready())
                    {
                        iPos = -1;
                        strLine  = null;
                        strLine = objBRdr.readLine().trim();
                        if (strLine == null)
                        {
                        }
                        else if (strLine.length() == 0)
                        {
                        }
                        else if (strLine.substring(0, 1).equals(";"))
                        {
                            if (strRemarks == null)
                                strRemarks = strLine.substring(1);
                            else if (strRemarks.length() == 0)
                                strRemarks = strLine.substring(1);
                            else
                                strRemarks = strRemarks + "\r\n" + strLine.substring(1);
                        }
                        else if (strLine.startsWith("[") && strLine.endsWith("]"))
                        {
                          
                            if (objSec != null) 
                                this.mhmapSections.put(strSection.trim(), objSec);
                            objSec = null;
                            strSection = strLine.substring(1, strLine.length() - 1);
                            objSec = new INISection(strSection.trim(), strRemarks);
                            strRemarks = null;
                        }
                        else if ((iPos = strLine.indexOf("=")) > 0 && objSec != null)
                        {
                          
                            objSec.setProperty(strLine.substring(0, iPos).trim(), 
                                                strLine.substring(iPos + 1).trim(), 
                                                strRemarks);
                            strRemarks = null;
                        }
                    }
                    if (objSec != null)
                        this.mhmapSections.put(strSection.trim(), objSec);
                    this.mblnLoaded = true;
                }
            }
        }
        catch (FileNotFoundException FNFExIgnore)
        {
            this.mhmapSections.clear();
        }
        catch (IOException IOExIgnore)
        {
            this.mhmapSections.clear();
        }
        catch (NullPointerException NPExIgnore)
        {
            this.mhmapSections.clear();
        }
        finally
        {
            if (objBRdr != null)
            {
                closeReader(objBRdr);
                objBRdr = null;
            }
            if (objFRdr != null)
            {
                closeReader(objFRdr);
                objFRdr = null;
            }
            if (objSec != null) objSec = null;
        }
    }


    private void closeReader(Reader pobjRdr)
    {
        if (pobjRdr == null) return;
        try
        {
            pobjRdr.close();
        }
        catch (IOException IOExIgnore)
        {
        }
    }


    private void closeWriter(Writer pobjWriter)
    {
        if (pobjWriter == null) return;

        try
        {
            pobjWriter.close();
        }
        catch (IOException IOExIgnore)
        {
        }
    }
    
 
    private boolean checkFile(String pstrFile)
    {
        boolean blnRet  = false;
        File    objFile = null;

        try
        {
            objFile = new File(pstrFile);
            blnRet = (objFile.exists() && objFile.isFile());
        }
        catch (Exception e)
        {
            blnRet = false;
        }
        finally
        {
            if (objFile != null) objFile = null;
        }
        return blnRet;
    }


    private String utilDateToStr(Date pdt, String pstrFmt)
    {
        String strRet = null;
        SimpleDateFormat dtFmt = null;

        try
        {
            dtFmt = new SimpleDateFormat(pstrFmt);
            strRet = dtFmt.format(pdt);
        }
        catch (Exception e)
        {
            strRet = null;
        }
        finally
        {
            if (dtFmt != null) dtFmt = null;
        }
        return strRet;
    }


    private String timeToStr(Timestamp pobjTS, String pstrFmt)
    {
        String strRet = null;
        SimpleDateFormat dtFmt = null;

        try
        {
            dtFmt = new SimpleDateFormat(pstrFmt);
            strRet = dtFmt.format(pobjTS);
        }
        catch (IllegalArgumentException  iae)
        {
            strRet = "";
        }
        catch (NullPointerException npe)
        {
            strRet = "";
        }
        finally
        {
            if (dtFmt != null) dtFmt = null;
        }
        return strRet;
    }

 
    private String delRemChars(String pstrSrc)
    {
        int    intPos = 0;

        if (pstrSrc == null) return null;
        while ((intPos = pstrSrc.indexOf(";")) >= 0)
        {
            if (intPos == 0)
                pstrSrc = pstrSrc.substring(intPos + 1);
            else if (intPos > 0)
                pstrSrc = pstrSrc.substring(0, intPos) + pstrSrc.substring(intPos + 1);
        }
        return pstrSrc;
    }

 
    private String addRemChars(String pstrSrc)
    {
        int intLen  = 2;
        int intPos  = 0;
        int intPrev = 0;

        String strLeft  = null;
        String strRight = null;

        if (pstrSrc == null) return null;
        while (intPos >= 0)
        {
            intLen = 2;
            intPos = pstrSrc.indexOf("\r\n", intPrev);
            if (intPos < 0)
            {
                intLen = 1;
                intPos = pstrSrc.indexOf("\n", intPrev);
                if (intPos < 0) intPos = pstrSrc.indexOf("\r", intPrev);
            }
            if (intPos == 0)
            {
                pstrSrc = ";\r\n" + pstrSrc.substring(intPos + intLen);
                intPrev = intPos + intLen + 1;
            }
            else if (intPos > 0)
            {
                strLeft = pstrSrc.substring(0, intPos);
                strRight = pstrSrc.substring(intPos + intLen);
                if (strRight == null)
                    pstrSrc = strLeft;
                else if (strRight.length() == 0)
                    pstrSrc = strLeft;
                else
                    pstrSrc = strLeft + "\r\n;" + strRight;
                intPrev = intPos + intLen + 1;
            }
        }
        if (!pstrSrc.substring(0, 1).equals(";"))
            pstrSrc = ";" + pstrSrc;
        pstrSrc = pstrSrc + "\r\n";
        return pstrSrc;
    }

    public static void main(String[] pstrArgs)
    {
        INIFile objINI = null;
        String  strFile = null;

        if (pstrArgs.length == 0) return;

        strFile = pstrArgs[0];
  
        objINI = new INIFile(strFile);


        objINI.setStringProperty("Folders", "folder1", "G:\\Temp", null);
        objINI.setStringProperty("Folders", "folder2", "G:\\Temp\\Backup", null);

  
        objINI.save();
        objINI = null;
    }


    private class INISection
    {
  
        private String mstrComment;

  
        private String mstrName;
        
  
        private LinkedHashMap<String, INIProperty> mhmapProps;

 
        public INISection(String pstrSection)
        {
            this.mstrName =  pstrSection;
            this.mhmapProps = new LinkedHashMap<String, INIProperty>();
        }

 
        public INISection(String pstrSection, String pstrComments)
        {
            this.mstrName =  pstrSection;
            this.mstrComment = delRemChars(pstrComments);
            this.mhmapProps = new LinkedHashMap<String, INIProperty>();
        }
        
 
        public String getSecComments()
        {
            return this.mstrComment;
        }

 
        public String getSecName()
        {
            return this.mstrName;
        }

 
        public void setSecComments(String pstrComments)
        {
            this.mstrComment = delRemChars(pstrComments);
        }

 
        public void setSecName(String pstrName)
        {
            this.mstrName = pstrName;
        }

 
        public void removeProperty(String pstrProp)
        {
            if (this.mhmapProps.containsKey(pstrProp))
                this.mhmapProps.remove(pstrProp);
        }

 
        public void setProperty(String pstrProp, String pstrValue, String pstrComments)
        {
            this.mhmapProps.put(pstrProp, new INIProperty(pstrProp, pstrValue, pstrComments));
        }

 
        public Map<String, INIProperty> getProperties()
        {
            return Collections.unmodifiableMap(this.mhmapProps);
        }

 
        public String[] getPropNames()
        {
            int      iCntr  = 0;
            String[] arrRet = null;
            Iterator<String> iter   = null;

            try
            {
                if (this.mhmapProps.size() > 0)
                {
                    arrRet = new String[this.mhmapProps.size()]; 
                    for (iter = this.mhmapProps.keySet().iterator();iter.hasNext();)
                    {
                        arrRet[iCntr] = (String) iter.next();
                        iCntr++;
                    }
                }
            }
            catch (NoSuchElementException NSEExIgnore)
            {
                arrRet = null;
            }
            return arrRet;
        }

 
        public INIProperty getProperty(String pstrProp)
        {
            INIProperty objRet = null;

            if (this.mhmapProps.containsKey(pstrProp))
                objRet = (INIProperty) this.mhmapProps.get(pstrProp);
            return objRet;
        }

 
        public String toString()
        {
            Set<String>          colKeys = null;
            String       strRet  = "";
            Iterator<String>     iter    = null;
            INIProperty  objProp = null;
            StringBuffer objBuf  = new StringBuffer();

            if (this.mstrComment != null)
                objBuf.append(addRemChars(this.mstrComment));
            objBuf.append("[" + this.mstrName + "]\r\n");
            colKeys = this.mhmapProps.keySet();
            if (colKeys != null)
            {
                iter = colKeys.iterator();
                if (iter != null)
                {
                    while (iter.hasNext())
                    {
                        objProp = (INIProperty) this.mhmapProps.get(iter.next());
                        objBuf.append(objProp.toString());
                        objBuf.append("\r\n");
                        objProp = null;
                    }
                }
            }
            strRet = objBuf.toString();

            objBuf  = null;
            iter    = null;
            colKeys = null;
            return strRet;
        }
    }


    private class INIProperty
    {

        private String mstrName;

        private String mstrValue;

        private String mstrComments;

        public INIProperty(String pstrName, String pstrValue)
        {
            this.mstrName = pstrName;
            this.mstrValue = pstrValue;
        }

 
        public INIProperty(String pstrName, String pstrValue, String pstrComments)
        {
            this.mstrName = pstrName;
            this.mstrValue = pstrValue;
            this.mstrComments = delRemChars(pstrComments);
        }

 
        public String getPropName()
        {
            return this.mstrName;
        }


        public String getPropValue()
        {
            int    intStart = 0;
            int    intEnd   = 0;
            String strVal   = null;
            String strVar   = null;
            String strRet   = null;

            strRet = this.mstrValue;
            intStart = strRet.indexOf("%");
            if (intStart >= 0)
            {
                intEnd = strRet.indexOf("%", intStart + 1);
                strVar = strRet.substring(intStart + 1, intEnd);
                strVal = mpropEnv.getProperty(strVar);
                if (strVal != null)
                {
                    strRet = strRet.substring(0, intStart) + strVal + 
                    		strRet.substring(intEnd + 1);
                }
            }
            return strRet;
        }

 
        public String getPropComments()
        {
            return this.mstrComments;
        }

 
        public void setPropName(String pstrName)
        {
            this.mstrName = pstrName;
        }

   
        public void setPropValue(String pstrValue)
        {
            this.mstrValue = pstrValue;
        }

 
        public void setPropComments(String pstrComments)
        {
            this.mstrComments = delRemChars(pstrComments);
        }

        public String toString()
        {
            String strRet = "";

            if (this.mstrComments != null)
                strRet = addRemChars(mstrComments);
            strRet = strRet + this.mstrName + " = " + this.mstrValue;
            return strRet;
        }
    }

    public void renameSection(String pstrSection,String newpstrSection, String pstrComments)
    {
        INISection objSec   = null;

        objSec = (INISection) this.mhmapSections.get(pstrSection);
        if (objSec != null)
        {
            objSec.mstrName = new String(newpstrSection);
        }
        objSec.setSecComments(delRemChars(pstrComments));
        objSec = null;
    }
}