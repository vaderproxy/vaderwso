import random
  
def wso_query(url, code):
    result = {
        "url":url,
        "post":{"c":code.encode('base64').strip()}
    }

    return result
