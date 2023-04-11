/**
 * Autogenerated by Avro
 *
 * DO NOT EDIT DIRECTLY
 */
package pojo.avro;

import org.apache.avro.generic.GenericArray;
import org.apache.avro.specific.SpecificData;
import org.apache.avro.util.Utf8;
import org.apache.avro.message.BinaryMessageEncoder;
import org.apache.avro.message.BinaryMessageDecoder;
import org.apache.avro.message.SchemaStore;

@org.apache.avro.specific.AvroGenerated
public class LoanDecision extends org.apache.avro.specific.SpecificRecordBase implements org.apache.avro.specific.SpecificRecord {
  private static final long serialVersionUID = -3692892284424519441L;


  public static final org.apache.avro.Schema SCHEMA$ = new org.apache.avro.Schema.Parser().parse("{\"type\":\"record\",\"name\":\"LoanDecision\",\"namespace\":\"pojo.avro\",\"fields\":[{\"name\":\"name\",\"type\":{\"type\":\"string\",\"avro.java.string\":\"String\"}},{\"name\":\"surname\",\"type\":{\"type\":\"string\",\"avro.java.string\":\"String\"}},{\"name\":\"amount\",\"type\":\"double\"},{\"name\":\"approved\",\"type\":\"boolean\"},{\"name\":\"source\",\"type\":{\"type\":\"string\",\"avro.java.string\":\"String\"}}]}");
  public static org.apache.avro.Schema getClassSchema() { return SCHEMA$; }

  private static final SpecificData MODEL$ = new SpecificData();

  private static final BinaryMessageEncoder<LoanDecision> ENCODER =
      new BinaryMessageEncoder<>(MODEL$, SCHEMA$);

  private static final BinaryMessageDecoder<LoanDecision> DECODER =
      new BinaryMessageDecoder<>(MODEL$, SCHEMA$);

  /**
   * Return the BinaryMessageEncoder instance used by this class.
   * @return the message encoder used by this class
   */
  public static BinaryMessageEncoder<LoanDecision> getEncoder() {
    return ENCODER;
  }

  /**
   * Return the BinaryMessageDecoder instance used by this class.
   * @return the message decoder used by this class
   */
  public static BinaryMessageDecoder<LoanDecision> getDecoder() {
    return DECODER;
  }

  /**
   * Create a new BinaryMessageDecoder instance for this class that uses the specified {@link SchemaStore}.
   * @param resolver a {@link SchemaStore} used to find schemas by fingerprint
   * @return a BinaryMessageDecoder instance for this class backed by the given SchemaStore
   */
  public static BinaryMessageDecoder<LoanDecision> createDecoder(SchemaStore resolver) {
    return new BinaryMessageDecoder<>(MODEL$, SCHEMA$, resolver);
  }

  /**
   * Serializes this LoanDecision to a ByteBuffer.
   * @return a buffer holding the serialized data for this instance
   * @throws java.io.IOException if this instance could not be serialized
   */
  public java.nio.ByteBuffer toByteBuffer() throws java.io.IOException {
    return ENCODER.encode(this);
  }

  /**
   * Deserializes a LoanDecision from a ByteBuffer.
   * @param b a byte buffer holding serialized data for an instance of this class
   * @return a LoanDecision instance decoded from the given buffer
   * @throws java.io.IOException if the given bytes could not be deserialized into an instance of this class
   */
  public static LoanDecision fromByteBuffer(
      java.nio.ByteBuffer b) throws java.io.IOException {
    return DECODER.decode(b);
  }

  private java.lang.String name;
  private java.lang.String surname;
  private double amount;
  private boolean approved;
  private java.lang.String source;

  /**
   * Default constructor.  Note that this does not initialize fields
   * to their default values from the schema.  If that is desired then
   * one should use <code>newBuilder()</code>.
   */
  public LoanDecision() {}

  /**
   * All-args constructor.
   * @param name The new value for name
   * @param surname The new value for surname
   * @param amount The new value for amount
   * @param approved The new value for approved
   * @param source The new value for source
   */
  public LoanDecision(java.lang.String name, java.lang.String surname, java.lang.Double amount, java.lang.Boolean approved, java.lang.String source) {
    this.name = name;
    this.surname = surname;
    this.amount = amount;
    this.approved = approved;
    this.source = source;
  }

  @Override
  public org.apache.avro.specific.SpecificData getSpecificData() { return MODEL$; }

  @Override
  public org.apache.avro.Schema getSchema() { return SCHEMA$; }

  // Used by DatumWriter.  Applications should not call.
  @Override
  public java.lang.Object get(int field$) {
    switch (field$) {
    case 0: return name;
    case 1: return surname;
    case 2: return amount;
    case 3: return approved;
    case 4: return source;
    default: throw new IndexOutOfBoundsException("Invalid index: " + field$);
    }
  }

  // Used by DatumReader.  Applications should not call.
  @Override
  @SuppressWarnings(value="unchecked")
  public void put(int field$, java.lang.Object value$) {
    switch (field$) {
    case 0: name = value$ != null ? value$.toString() : null; break;
    case 1: surname = value$ != null ? value$.toString() : null; break;
    case 2: amount = (java.lang.Double)value$; break;
    case 3: approved = (java.lang.Boolean)value$; break;
    case 4: source = value$ != null ? value$.toString() : null; break;
    default: throw new IndexOutOfBoundsException("Invalid index: " + field$);
    }
  }

  /**
   * Gets the value of the 'name' field.
   * @return The value of the 'name' field.
   */
  public java.lang.String getName() {
    return name;
  }


  /**
   * Sets the value of the 'name' field.
   * @param value the value to set.
   */
  public void setName(java.lang.String value) {
    this.name = value;
  }

  /**
   * Gets the value of the 'surname' field.
   * @return The value of the 'surname' field.
   */
  public java.lang.String getSurname() {
    return surname;
  }


  /**
   * Sets the value of the 'surname' field.
   * @param value the value to set.
   */
  public void setSurname(java.lang.String value) {
    this.surname = value;
  }

  /**
   * Gets the value of the 'amount' field.
   * @return The value of the 'amount' field.
   */
  public double getAmount() {
    return amount;
  }


  /**
   * Sets the value of the 'amount' field.
   * @param value the value to set.
   */
  public void setAmount(double value) {
    this.amount = value;
  }

  /**
   * Gets the value of the 'approved' field.
   * @return The value of the 'approved' field.
   */
  public boolean getApproved() {
    return approved;
  }


  /**
   * Sets the value of the 'approved' field.
   * @param value the value to set.
   */
  public void setApproved(boolean value) {
    this.approved = value;
  }

  /**
   * Gets the value of the 'source' field.
   * @return The value of the 'source' field.
   */
  public java.lang.String getSource() {
    return source;
  }


  /**
   * Sets the value of the 'source' field.
   * @param value the value to set.
   */
  public void setSource(java.lang.String value) {
    this.source = value;
  }

  /**
   * Creates a new LoanDecision RecordBuilder.
   * @return A new LoanDecision RecordBuilder
   */
  public static pojo.avro.LoanDecision.Builder newBuilder() {
    return new pojo.avro.LoanDecision.Builder();
  }

  /**
   * Creates a new LoanDecision RecordBuilder by copying an existing Builder.
   * @param other The existing builder to copy.
   * @return A new LoanDecision RecordBuilder
   */
  public static pojo.avro.LoanDecision.Builder newBuilder(pojo.avro.LoanDecision.Builder other) {
    if (other == null) {
      return new pojo.avro.LoanDecision.Builder();
    } else {
      return new pojo.avro.LoanDecision.Builder(other);
    }
  }

  /**
   * Creates a new LoanDecision RecordBuilder by copying an existing LoanDecision instance.
   * @param other The existing instance to copy.
   * @return A new LoanDecision RecordBuilder
   */
  public static pojo.avro.LoanDecision.Builder newBuilder(pojo.avro.LoanDecision other) {
    if (other == null) {
      return new pojo.avro.LoanDecision.Builder();
    } else {
      return new pojo.avro.LoanDecision.Builder(other);
    }
  }

  /**
   * RecordBuilder for LoanDecision instances.
   */
  @org.apache.avro.specific.AvroGenerated
  public static class Builder extends org.apache.avro.specific.SpecificRecordBuilderBase<LoanDecision>
    implements org.apache.avro.data.RecordBuilder<LoanDecision> {

    private java.lang.String name;
    private java.lang.String surname;
    private double amount;
    private boolean approved;
    private java.lang.String source;

    /** Creates a new Builder */
    private Builder() {
      super(SCHEMA$, MODEL$);
    }

    /**
     * Creates a Builder by copying an existing Builder.
     * @param other The existing Builder to copy.
     */
    private Builder(pojo.avro.LoanDecision.Builder other) {
      super(other);
      if (isValidValue(fields()[0], other.name)) {
        this.name = data().deepCopy(fields()[0].schema(), other.name);
        fieldSetFlags()[0] = other.fieldSetFlags()[0];
      }
      if (isValidValue(fields()[1], other.surname)) {
        this.surname = data().deepCopy(fields()[1].schema(), other.surname);
        fieldSetFlags()[1] = other.fieldSetFlags()[1];
      }
      if (isValidValue(fields()[2], other.amount)) {
        this.amount = data().deepCopy(fields()[2].schema(), other.amount);
        fieldSetFlags()[2] = other.fieldSetFlags()[2];
      }
      if (isValidValue(fields()[3], other.approved)) {
        this.approved = data().deepCopy(fields()[3].schema(), other.approved);
        fieldSetFlags()[3] = other.fieldSetFlags()[3];
      }
      if (isValidValue(fields()[4], other.source)) {
        this.source = data().deepCopy(fields()[4].schema(), other.source);
        fieldSetFlags()[4] = other.fieldSetFlags()[4];
      }
    }

    /**
     * Creates a Builder by copying an existing LoanDecision instance
     * @param other The existing instance to copy.
     */
    private Builder(pojo.avro.LoanDecision other) {
      super(SCHEMA$, MODEL$);
      if (isValidValue(fields()[0], other.name)) {
        this.name = data().deepCopy(fields()[0].schema(), other.name);
        fieldSetFlags()[0] = true;
      }
      if (isValidValue(fields()[1], other.surname)) {
        this.surname = data().deepCopy(fields()[1].schema(), other.surname);
        fieldSetFlags()[1] = true;
      }
      if (isValidValue(fields()[2], other.amount)) {
        this.amount = data().deepCopy(fields()[2].schema(), other.amount);
        fieldSetFlags()[2] = true;
      }
      if (isValidValue(fields()[3], other.approved)) {
        this.approved = data().deepCopy(fields()[3].schema(), other.approved);
        fieldSetFlags()[3] = true;
      }
      if (isValidValue(fields()[4], other.source)) {
        this.source = data().deepCopy(fields()[4].schema(), other.source);
        fieldSetFlags()[4] = true;
      }
    }

    /**
      * Gets the value of the 'name' field.
      * @return The value.
      */
    public java.lang.String getName() {
      return name;
    }


    /**
      * Sets the value of the 'name' field.
      * @param value The value of 'name'.
      * @return This builder.
      */
    public pojo.avro.LoanDecision.Builder setName(java.lang.String value) {
      validate(fields()[0], value);
      this.name = value;
      fieldSetFlags()[0] = true;
      return this;
    }

    /**
      * Checks whether the 'name' field has been set.
      * @return True if the 'name' field has been set, false otherwise.
      */
    public boolean hasName() {
      return fieldSetFlags()[0];
    }


    /**
      * Clears the value of the 'name' field.
      * @return This builder.
      */
    public pojo.avro.LoanDecision.Builder clearName() {
      name = null;
      fieldSetFlags()[0] = false;
      return this;
    }

    /**
      * Gets the value of the 'surname' field.
      * @return The value.
      */
    public java.lang.String getSurname() {
      return surname;
    }


    /**
      * Sets the value of the 'surname' field.
      * @param value The value of 'surname'.
      * @return This builder.
      */
    public pojo.avro.LoanDecision.Builder setSurname(java.lang.String value) {
      validate(fields()[1], value);
      this.surname = value;
      fieldSetFlags()[1] = true;
      return this;
    }

    /**
      * Checks whether the 'surname' field has been set.
      * @return True if the 'surname' field has been set, false otherwise.
      */
    public boolean hasSurname() {
      return fieldSetFlags()[1];
    }


    /**
      * Clears the value of the 'surname' field.
      * @return This builder.
      */
    public pojo.avro.LoanDecision.Builder clearSurname() {
      surname = null;
      fieldSetFlags()[1] = false;
      return this;
    }

    /**
      * Gets the value of the 'amount' field.
      * @return The value.
      */
    public double getAmount() {
      return amount;
    }


    /**
      * Sets the value of the 'amount' field.
      * @param value The value of 'amount'.
      * @return This builder.
      */
    public pojo.avro.LoanDecision.Builder setAmount(double value) {
      validate(fields()[2], value);
      this.amount = value;
      fieldSetFlags()[2] = true;
      return this;
    }

    /**
      * Checks whether the 'amount' field has been set.
      * @return True if the 'amount' field has been set, false otherwise.
      */
    public boolean hasAmount() {
      return fieldSetFlags()[2];
    }


    /**
      * Clears the value of the 'amount' field.
      * @return This builder.
      */
    public pojo.avro.LoanDecision.Builder clearAmount() {
      fieldSetFlags()[2] = false;
      return this;
    }

    /**
      * Gets the value of the 'approved' field.
      * @return The value.
      */
    public boolean getApproved() {
      return approved;
    }


    /**
      * Sets the value of the 'approved' field.
      * @param value The value of 'approved'.
      * @return This builder.
      */
    public pojo.avro.LoanDecision.Builder setApproved(boolean value) {
      validate(fields()[3], value);
      this.approved = value;
      fieldSetFlags()[3] = true;
      return this;
    }

    /**
      * Checks whether the 'approved' field has been set.
      * @return True if the 'approved' field has been set, false otherwise.
      */
    public boolean hasApproved() {
      return fieldSetFlags()[3];
    }


    /**
      * Clears the value of the 'approved' field.
      * @return This builder.
      */
    public pojo.avro.LoanDecision.Builder clearApproved() {
      fieldSetFlags()[3] = false;
      return this;
    }

    /**
      * Gets the value of the 'source' field.
      * @return The value.
      */
    public java.lang.String getSource() {
      return source;
    }


    /**
      * Sets the value of the 'source' field.
      * @param value The value of 'source'.
      * @return This builder.
      */
    public pojo.avro.LoanDecision.Builder setSource(java.lang.String value) {
      validate(fields()[4], value);
      this.source = value;
      fieldSetFlags()[4] = true;
      return this;
    }

    /**
      * Checks whether the 'source' field has been set.
      * @return True if the 'source' field has been set, false otherwise.
      */
    public boolean hasSource() {
      return fieldSetFlags()[4];
    }


    /**
      * Clears the value of the 'source' field.
      * @return This builder.
      */
    public pojo.avro.LoanDecision.Builder clearSource() {
      source = null;
      fieldSetFlags()[4] = false;
      return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public LoanDecision build() {
      try {
        LoanDecision record = new LoanDecision();
        record.name = fieldSetFlags()[0] ? this.name : (java.lang.String) defaultValue(fields()[0]);
        record.surname = fieldSetFlags()[1] ? this.surname : (java.lang.String) defaultValue(fields()[1]);
        record.amount = fieldSetFlags()[2] ? this.amount : (java.lang.Double) defaultValue(fields()[2]);
        record.approved = fieldSetFlags()[3] ? this.approved : (java.lang.Boolean) defaultValue(fields()[3]);
        record.source = fieldSetFlags()[4] ? this.source : (java.lang.String) defaultValue(fields()[4]);
        return record;
      } catch (org.apache.avro.AvroMissingFieldException e) {
        throw e;
      } catch (java.lang.Exception e) {
        throw new org.apache.avro.AvroRuntimeException(e);
      }
    }
  }

  @SuppressWarnings("unchecked")
  private static final org.apache.avro.io.DatumWriter<LoanDecision>
    WRITER$ = (org.apache.avro.io.DatumWriter<LoanDecision>)MODEL$.createDatumWriter(SCHEMA$);

  @Override public void writeExternal(java.io.ObjectOutput out)
    throws java.io.IOException {
    WRITER$.write(this, SpecificData.getEncoder(out));
  }

  @SuppressWarnings("unchecked")
  private static final org.apache.avro.io.DatumReader<LoanDecision>
    READER$ = (org.apache.avro.io.DatumReader<LoanDecision>)MODEL$.createDatumReader(SCHEMA$);

  @Override public void readExternal(java.io.ObjectInput in)
    throws java.io.IOException {
    READER$.read(this, SpecificData.getDecoder(in));
  }

  @Override protected boolean hasCustomCoders() { return true; }

  @Override public void customEncode(org.apache.avro.io.Encoder out)
    throws java.io.IOException
  {
    out.writeString(this.name);

    out.writeString(this.surname);

    out.writeDouble(this.amount);

    out.writeBoolean(this.approved);

    out.writeString(this.source);

  }

  @Override public void customDecode(org.apache.avro.io.ResolvingDecoder in)
    throws java.io.IOException
  {
    org.apache.avro.Schema.Field[] fieldOrder = in.readFieldOrderIfDiff();
    if (fieldOrder == null) {
      this.name = in.readString();

      this.surname = in.readString();

      this.amount = in.readDouble();

      this.approved = in.readBoolean();

      this.source = in.readString();

    } else {
      for (int i = 0; i < 5; i++) {
        switch (fieldOrder[i].pos()) {
        case 0:
          this.name = in.readString();
          break;

        case 1:
          this.surname = in.readString();
          break;

        case 2:
          this.amount = in.readDouble();
          break;

        case 3:
          this.approved = in.readBoolean();
          break;

        case 4:
          this.source = in.readString();
          break;

        default:
          throw new java.io.IOException("Corrupt ResolvingDecoder.");
        }
      }
    }
  }
}










