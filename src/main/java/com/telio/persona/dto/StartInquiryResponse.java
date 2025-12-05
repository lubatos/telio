//package com.telio.persona.dto;
//
//public class StartInquiryResponse {
//}

package com.telio.persona.dto;

//public class StartInquiryResponse {
//    public String inquiryId;
//    public StartInquiryResponse(String inquiryId) {
//        this.inquiryId = inquiryId;
//    }
//}



public class StartInquiryResponse {
    public String inquiryId;
    public String clientToken;
    public String templateId;

    public StartInquiryResponse(String inquiryId, String clientToken, String templateId) {
        this.inquiryId = inquiryId;
        this.clientToken = clientToken;
        this.templateId = templateId;
    }
}
