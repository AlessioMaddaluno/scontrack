import azure.functions as func
from azure.core.credentials import AzureKeyCredential
from azure.ai.formrecognizer import DocumentAnalysisClient
import logging
import json
import os
from dateutil import parser

app = func.FunctionApp(http_auth_level=func.AuthLevel.FUNCTION)

@app.route(route="receipt_analyzer")
def receipt_analyzer(req: func.HttpRequest) -> func.HttpResponse:
    logging.info('Nuova richiesta ricevuta')

    blob_name = req.params.get('blobName')
    if not blob_name:
        try:
            req_body = req.get_json()
            blob_name = req_body.get('blobName')
        except ValueError:
            pass

    if not blob_name:
        return func.HttpResponse(
            "Devi fornire un nome di blob",
            status_code=400
        )

    # Blob URL
    storage_account = 'scontracksa'
    blob_container = 'scontrack-images'
    url = f'https://{storage_account}.blob.core.windows.net/{blob_container}/{blob_name}'

    logging.info(url)

    result = analyze_receipt(url)
    if result['is_valid'] == False:
        logging.info('Risultato non valido')

    return func.HttpResponse(
        json.dumps(result),
        status_code=200,
        mimetype="application/json"
    )

def analyze_receipt(url):
    result = {'is_valid': False}

    endpoint = "https://westeurope.api.cognitive.microsoft.com/"
    ai_analyzer_key = os.environ['AI_ANALYZER_KEY']

    accuracy_threshold = 0.70

    if not ai_analyzer_key:
        logging.error('Chiave di analisi mancante nelle variabili di ambiente')
        return result

    document_analysis_client = DocumentAnalysisClient(
        endpoint=endpoint, credential=AzureKeyCredential(ai_analyzer_key)
    )

    try:
        poller = document_analysis_client.begin_analyze_document_from_url("prebuilt-receipt", url)
        receipts = poller.result()

        if not receipts.documents:
            return result

        receipt = receipts.documents[0]
        result['receipt_type'] = extract_type(receipt.doc_type)

        if receipt.fields:
            extract_and_validate(result, 'merchant_name', receipt.fields.get("MerchantName"), accuracy_threshold)
            extract_and_validate(result, 'transaction_date', receipt.fields.get("TransactionDate"), accuracy_threshold)
            extract_and_validate(result, 'total', receipt.fields.get("Total"), accuracy_threshold)

    except Exception as e:
        logging.error(f'Errore durante l\'analisi del documento: {e}')

    return result

def extract_and_validate(result, key, field, threshold):
    if field:
        if key == 'transaction_date':
            logging.info(field.content)
            result[key] = {'value': str(parse_date_string(field.content)), 'confidence': field.confidence}
        else:
            result[key] = {'value': str(field.value), 'confidence': field.confidence}
        
        if field.confidence > threshold:
            result['is_valid'] = True
    else:
        result[key] = None
        result['is_valid'] = False


def extract_type(doc_type):
    type_mapping = {
        'receipt.retailMeal': 'FOOD',
        'receipt.creditCard': 'CREDIT_CARD',
        'receipt.gas': 'GAS',
        'receipt.parking': 'PARKING',
        'receipt.hotel': 'HOTEL'
    }
    return type_mapping.get(doc_type, 'OTHER')

def parse_date_string(date_str):
    date_obj = parser.parse(date_str,dayfirst=True)
    return date_obj.strftime("%Y-%m-%d")