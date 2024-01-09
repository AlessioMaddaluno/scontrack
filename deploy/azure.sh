# az login

AZ_LOCATION=westeurope
AZ_RESOURCE_GROUP=scontrack
AZ_STORAGE_ACCOUNT=scontracksa
AZ_DATABASE_NAME=scontrack-db
AZ_DATABASE_USER=bot
AZ_DATABASE_PASSWORD=<CHANGE-ME> 
AZ_STORAGE_CONTAINER_NAME=scontrack-images
AZ_REGISTRY_NAME=scontrackregistry
DOCKER_IMAGE_NAME=scontrack-bot

echo "Creating Resource Group"
az group create \
    --name $AZ_RESOURCE_GROUP \
    --location $AZ_LOCATION 

echo "Creating Storage Account"
az storage account create \
  --name $AZ_STORAGE_ACCOUNT \
  --location $AZ_LOCATION \
  --resource-group $AZ_RESOURCE_GROUP \
  --sku Standard_LRS \
  --allow-blob-public-access

echo "Creating Storage Container"
az storage container create \
    --account-name $AZ_STORAGE_ACCOUNT \
    --name $AZ_STORAGE_CONTAINER_NAME

echo "Creating Container Registry"
az acr create \
    --resource-group $AZ_RESOURCE_GROUP \
    --name $AZ_REGISTRY_NAME \
    --sku Basic 

# enable access

echo "Creating Cogitive Service"
az cognitiveservices account create \
    --resource-group $AZ_RESOURCE_GROUP \
    --name scontrack-receipt-analyzer \
    --location $AZ_LOCATION \
    --sku F0 \
    --kind FormRecognizer

echo "Creating Function App"
az functionapp create \
    --resource-group $AZ_RESOURCE_GROUP \
    --consumption-plan-location $AZ_LOCATION \
    --name scontrack-receipt-analyzer \
    --runtime python \
    --runtime-version 3.10 \
    --functions-version 4 \
    --storage-account $AZ_STORAGE_ACCOUNT \
    --os linux

echo "Setting Function App"
az functionapp config appsettings set \
  --name scontrack-receipt-analyzer \
  --resource-group $AZ_RESOURCE_GROUP \
  --settings "AI_ANALYZER_KEY=$(az cognitiveservices account keys list --name scontrack-receipt-analyzer -g $AZ_RESOURCE_GROUP --query 'key1' -o tsv)"

# func azure functionapp publish scontrack-receipt-analyzer

# Crea SQL Server
az sql server create \
    --resource-group $AZ_RESOURCE_GROUP \
    --name $AZ_DATABASE_NAME \
    --location $AZ_LOCATION \
    --admin-user $AZ_DATABASE_USER \
    --admin-password $AZ_DATABASE_PASSWORD 

az sql db create \
    --resource-group $AZ_RESOURCE_GROUP \
    --name $AZ_DATABASE_NAME \
    --server $AZ_DATABASE_NAME 



# docker build . -t scontrack-bot
# az acr login --name scontrackregistry
# az acr update -n scontrackregistry --admin-enabled true
# docker tag scontrack-bot scontrackregistry.azurecr.io/scontrack-bot:0.1
 docker push scontrackregistry.azurecr.io/scontrack-bot:0.1
 
#echo "Creating container"
az container create \
    --resource-group $AZ_RESOURCE_GROUP \
    --name scontrack-telegram-bot \
    --image $AZ_REGISTRY_NAME.azurecr.io/$DOCKER_IMAGE_NAME:0.1 \
    --cpu 1 \
    --memory 1.5 \
    --registry-login-server $AZ_REGISTRY_NAME.azurecr.io \
    --registry-username $AZ_REGISTRY_NAME \
    --registry-password $(az acr credential show --name $AZ_REGISTRY_NAME --query "passwords[0].value" --output tsv) \
    --environment-variables \
    AZ_DATABASE_USER=$AZ_DATABASE_USER\
    AZ_DATABASE_PASSWORD=$AZ_DATABASE_PASSWORD\
    TELEGRAM_TOKEN=6867808049:AAEHutFRy-z0Eq1Nqq4Y0TFoh6NO9I_EEZc\
    AZURE_BLOB_STORAGE_KEY=G7hKoGZ0u4ZXVSJOngEzRzJuNSr1YqKwGtSGSMzg0QPsaYYvbG3Cr2vxC+uaLDVPPEiS7AwKqbKA+ASt06uv+w==\
    AZURE_RECEIPTS_AI_ENDPOINT=https://scontrack-receipt-analyzer.azurewebsites.net/api/recepit_analyzer?code=9H3CLgzStMQmBeKVaoZ5M5sqCDxOh8QP8BQswy-miGX1AzFukF99Pg== 