package lt.itdbaltics.camel.component.existdb;

public enum QueryServiceType {
    XPATH("XPathQueryService", "1.0"),
    XQUERY("XQueryService", "1.0");

    private String version;
    private String serviceName;

    QueryServiceType(String serviceName, String version) {
        this.serviceName = serviceName;
        this.version = version;
    }

    public String getVersion() {
        return version;
    }

    public String getServiceName() {
        return serviceName;
    }

    @Override
    public String toString() {
        StringBuilder string = new StringBuilder();
        string.append("{serviceName: '");
        string.append(serviceName);
        string.append("'}, {");
        string.append("serviceVersion: '");
        string.append(version);
        string.append("'};");

        return string.toString();
    }
}
