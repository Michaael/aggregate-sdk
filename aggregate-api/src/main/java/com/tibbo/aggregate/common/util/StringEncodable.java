package com.tibbo.aggregate.common.util;

import com.tibbo.aggregate.common.datatable.encoding.*;

public interface StringEncodable
{
  StringBuilder encode(StringBuilder sb, ClassicEncodingSettings settings, Boolean isTransferEncode, Integer encodeLevel);
}
